/**
 * Copyright &copy; 2012-2016 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.thinkgem.jeesite.modules.gen.web;

import com.thinkgem.jeesite.common.persistence.Page;
import com.thinkgem.jeesite.common.utils.StringUtils;
import com.thinkgem.jeesite.common.web.BaseController;
import com.thinkgem.jeesite.modules.gen.entity.GenBusTable;
import com.thinkgem.jeesite.modules.gen.entity.GenTable;
import com.thinkgem.jeesite.modules.gen.service.GenTableService;
import com.thinkgem.jeesite.modules.gen.util.GenUtils;
import com.thinkgem.jeesite.modules.sys.entity.Dict;
import com.thinkgem.jeesite.modules.sys.entity.User;
import com.thinkgem.jeesite.modules.sys.service.DictService;
import com.thinkgem.jeesite.modules.sys.utils.UserUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 业务表Controller
 *
 * @author ThinkGem
 * @version 2013-10-15
 */
@Controller
@RequestMapping(value = "${adminPath}/gen/genTable")
public class GenTableController extends BaseController {

    @Autowired
    private GenTableService genTableService;
    @Autowired
    private DictService dictService;

    @ModelAttribute
    public GenTable get(@RequestParam(required = false) String id) {
        if (StringUtils.isNotBlank(id)) {
            return genTableService.get(id);
        } else {
            return new GenTable();
        }
    }

    @RequiresPermissions("gen:genTable:view")
    @RequestMapping(value = {"list", ""})
    public String list(GenTable genTable, HttpServletRequest request, HttpServletResponse response, Model model) {
        User user = UserUtils.getUser();
        if (!user.isAdmin()) {
            genTable.setCreateBy(user);
        }
        Page<GenTable> page = genTableService.find(new Page<GenTable>(request, response), genTable);
        model.addAttribute("page", page);
        return "modules/gen/genTableList";
    }

    @RequiresPermissions("gen:genTable:view")
    @RequestMapping(value = "form")
    public String form(GenTable genTable, Model model) {
        // 获取物理表列表
        List<GenTable> tableList = genTableService.findTableListFormDb(new GenTable());
        model.addAttribute("tableList", tableList);
        if (null != genTable.getId()) {
            genTable = genTableService.get(genTable.getId());
        }
        model.addAttribute("genTable", genTable);
        model.addAttribute("config", GenUtils.getConfig());
        return "modules/gen/genTableForm";
    }

    @RequiresPermissions("gen:genTable:view")
    @RequestMapping(value = "showTableInfo")
    public String showTableInfo(GenTable genTable, Model model) {
        // 获取物理表列表
        List<GenTable> tableList = genTableService.findTableListFormDb(new GenTable());
        model.addAttribute("tableList", tableList);
       String name=genTable.getName();
        genTable = genTableService.getByName(genTable.getName());
        if (null != genTable) {
            genTable = genTableService.get(genTable.getId());
        } else {
            genTable=new GenTable();
            genTable.setName(name);
            genTable = genTableService.getTableFormDb(genTable);
        }
        model.addAttribute("genTable", genTable);
        model.addAttribute("config", GenUtils.getConfig());
        return "modules/gen/genTableForm";
    }


    @RequiresPermissions("gen:genTable:edit")
    @RequestMapping(value = "save")
    public String save(GenTable genTable, Model model, RedirectAttributes redirectAttributes) {
        if (!beanValidator(model, genTable)) {
            return form(genTable, model);
        }
        // 验证表是否已经存在
        /*if (StringUtils.isBlank(genTable.getId()) && !genTableService.checkTableName(genTable.getName())) {
            addMessage(model, "保存失败！" + genTable.getName() + " 表已经存在！");
            genTable.setName("");
            return form(genTable, model);
        }*/
        genTableService.save(genTable);
        //修改业务表表结构

        addMessage(redirectAttributes, "保存业务表'" + genTable.getName() + "'成功");
        return "redirect:" + adminPath + "/gen/genTable/?repage";
    }

    @RequiresPermissions("gen:genTable:edit")
    @RequestMapping(value = "delete")
    public String delete(GenTable genTable, RedirectAttributes redirectAttributes) {
        genTableService.delete(genTable);
        addMessage(redirectAttributes, "删除业务表成功");
        return "redirect:" + adminPath + "/gen/genTable/?repage";
    }

    /**
     * 加载选择业务表页面
     *
     * @return
     */

    @RequiresPermissions("gen:genTable:view")
    @RequestMapping(value = {"genBusTable"})
    public String genBusTable(Model model) {
        List<GenTable> tableList = genTableService.findGenTableList();
        model.addAttribute("tableList", tableList);
        return "modules/gen/genBusTable";
    }


    @RequiresPermissions("gen:genTable:view")
    @RequestMapping(value = "showBusTable")
    public String showBusTable(GenTable genTable, Model model) {
        genTable = genTableService.getByName(genTable.getName());
        if (null != genTable.getId()) {
            genTable = genTableService.get(genTable.getId());
        }
        model.addAttribute("genTable", genTable);
        Dict dict = new Dict();
        dict.setType("theme");
        List<Dict> dictList = dictService.findList(dict);
        Map map = new HashMap();
        for (Dict entity : dictList) {
            map.put(entity.getLabel(), entity.getLabel());
        }
        model.addAttribute("dictList", map);
        model.addAttribute("config", GenUtils.getConfig());
        return "modules/gen/genBusTableInfo";
    }


    @RequestMapping(value = "busTableSave")
    //@ResponseBody
    public String busTableSave(GenTable genTable, RedirectAttributes redirectAttributes) {
        genTableService.saveBusTableData(genTable);
        addMessage(redirectAttributes, "删除业务表成功");
        return "redirect:" + adminPath + "/gen/genTable/genBusTableList";
    }


    @RequiresPermissions("gen:genTable:view")
    @RequestMapping(value = "genBusTableList")
    public String genBusTableList(GenBusTable genBusTable, HttpServletRequest request, HttpServletResponse response,
                                  Model model) {
        //查询业务表字段选择结果列表
        Dict dict = new Dict();
        dict.setType("theme");
        List<Dict> dictList = dictService.findList(dict);
        Map map = new HashMap();
        for (Dict entity : dictList) {
            map.put(entity.getLabel(), entity.getLabel());
        }
        model.addAttribute("dictList", map);
        Page<GenBusTable> page = genTableService.genBusTableList(new Page<GenBusTable>(request, response), genBusTable);
        model.addAttribute("page", page);
        return "modules/gen/genBusTableList";


    }


}
