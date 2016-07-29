package com.lcw.herakles.platform.demo.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.groups.Default;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.lcw.herakles.platform.common.cache.redis.repository.BaseRedisDao;
import com.lcw.herakles.platform.common.constant.ApplicationConstant;
import com.lcw.herakles.platform.common.controller.BaseController;
import com.lcw.herakles.platform.common.dto.ResultDto;
import com.lcw.herakles.platform.common.dto.ResultDtoFactory;
import com.lcw.herakles.platform.common.dto.annotation.OnValid;
import com.lcw.herakles.platform.common.dto.datatable.DataTablesResponseDto;
import com.lcw.herakles.platform.common.enumeration.EErrorCode;
import com.lcw.herakles.platform.common.enumeration.EnumOption;
import com.lcw.herakles.platform.common.exception.BizServiceException;
import com.lcw.herakles.platform.common.util.DateUtils;
import com.lcw.herakles.platform.common.util.EnumHelper;
import com.lcw.herakles.platform.common.util.web.WebUtil;
import com.lcw.herakles.platform.demo.dto.ProductDto;
import com.lcw.herakles.platform.demo.dto.ProductDto.CreateProduct;
import com.lcw.herakles.platform.demo.dto.req.ProductReqDto;
import com.lcw.herakles.platform.demo.dto.req.ProductSearchDto;
import com.lcw.herakles.platform.demo.entity.ProductPo;
import com.lcw.herakles.platform.demo.enumeration.EProductCagetory;
import com.lcw.herakles.platform.demo.service.ProductQueryService;
import com.lcw.herakles.platform.demo.service.ProductService;
import com.lcw.herakles.platform.system.files.consts.FileConsts;
import com.lcw.herakles.platform.system.files.consts.FileTemplateConsts;
import com.lcw.herakles.platform.system.mail.service.EmailSerivce;
import com.lcw.herakles.platform.system.security.SecurityContext;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * Class Name: ProductController Description: TODO
 * 
 * @author chenwulou
 * 
 */
@Api(value = "product", description = "产品管理")
@Controller
@RequestMapping(value = "product")
public class ProductController extends BaseController {

	@Autowired
	private BaseRedisDao baseRedisDao;
	@Autowired
	private EmailSerivce emailSerivce;
	@Autowired
	private ProductService productService;
	@Autowired
	private SecurityContext securityContext;
	@Autowired
	private ProductQueryService productQueryService;
	// @Autowired
	// private ProductInfoExcelExportService productInfoExcelExportService;

	@ApiOperation(notes = "addGroup", httpMethod = "GET", value = "添加一个新的群组", response = String.class)
	@RequestMapping(value = "test", method = RequestMethod.GET)
	public String test() {
		return "product/test";
	}

	@SuppressWarnings({ "unused", "unchecked" })
	@RequestMapping(value = "test-redis", method = RequestMethod.GET)
	@ApiOperation(value = "测试redis", httpMethod = "GET", response = String.class)
	public String testRedis() {
		List<String> listStr = new ArrayList<>();
		List<String> listStr1 = new ArrayList<>();
		listStr.add("1");
		listStr.add("2");
		listStr.add("3");
		baseRedisDao.addStr("USER", "LCW", "楼晨武");
		baseRedisDao.getStr("USER", "LCW");
		baseRedisDao.addStr("USER", "LIST", listStr);
		listStr1 = (List<String>) baseRedisDao.getStr("USER", "LIST");
		return "product/test";
	}

	@SuppressWarnings("unused")
	@RequestMapping(value = "test-email", method = RequestMethod.GET)
	@ApiOperation(value = "测试email", httpMethod = "GET", response = String.class)
	public String testEmail() {
		String title = "邮箱绑定";
		String templateName = "modifyEmail";
		Map<String, Object> model = new HashMap<String, Object>();
		String expireDate = DateUtils.formatDate(DateUtils.getDate(this.getDefaultExpireDttm(), "yyyyMMddHHmmss"),
				"yyyy-MM-dd hh:mm:ss");
		model.put("expireDate", expireDate);
		model.put("nickName", securityContext.getCurrentUser().getNickName());
		model.put("url", "www.bing.com");
		model.put("isUnbound", "xxxxx");
		emailSerivce.sendSimpleEmail("标题", "内容", "4949344008@qq.com");
		// emailSerivce.sendHtmlEmail(title, templateName, model,
		// "wo6x5a1@163.com");
		return "product/test";
	}

	private String getDefaultExpireDttm() {
		Date expireDate = DateUtils.add(new Date(), Calendar.HOUR_OF_DAY, 24);
		return DateUtils.formatDate(expireDate, "yyyyMMddHHmmss");
	}

	@ApiOperation(value = "测试模板下载", httpMethod = "GET", response = ModelAndView.class)
	@RequestMapping(value = "test-temp-form", method = RequestMethod.GET)
	public ModelAndView printApplicationForm() {
		ModelAndView model = new ModelAndView(FileTemplateConsts.REDIRECT);
		model.addObject("fileName", FileTemplateConsts.TEST_TEMP);
		model.addObject("showFileName", FileTemplateConsts.TEST_TEMP_NAME);
		model.addObject("suffixes", FileConsts.DOC);
		model.addObject("filePath", FileTemplateConsts.WORD_PATH);
		return model;
	}

	/**
	 * Description: render home page
	 *
	 * @param model
	 * @return
	 */
	@RequiresPermissions("product:view")
	@RequestMapping(value = "view", method = RequestMethod.GET)
	@ApiOperation(value = "页面跳转", httpMethod = "GET", response = String.class)
	public String home(Model model) {
		// model.addAttribute("categoryList",
		// EnumHelper.inspectConstants(EProductCagetory.class));
		model.addAttribute("categoryList", this.getProductCagetory());
		return "product/product_list";
	}

	/**
	 * Description: search product list on the home page
	 *
	 * @param request
	 * @return
	 */
	// @RequiresPermissions("product:list")
	@RequestMapping(value = "list", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "获得产品列表", httpMethod = "POST", responseContainer = "DataTablesResponseDto<ProductDto> resp")
	public DataTablesResponseDto<ProductDto> search(@RequestBody ProductSearchDto request) {
		DataTablesResponseDto<ProductDto> resp = productQueryService.searchProduct(request);
		return resp;
	}

//	@RequestMapping(value = "/export-xls", method = RequestMethod.POST)
//	public ModelAndView exportBrokerInvesterXls(ProductSearchDto request) {
//		List<ProductDto> dataList = productQueryService.searchProduct(request).getData();
//		String fileName = "test.xls";
//		String tempPath = "excel/report/product_repo.xls";
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put(ApplicationConstant.REPORT_DATA, dataList);
//		map.put(ApplicationConstant.REPORT_FILE_NAME, fileName);
//		map.put(ApplicationConstant.REPORT_TEMP_PATH, tempPath);
//		return new ModelAndView(productInfoExcelExportService, map);
//	}

	/**
	 * Description: render add-product page
	 *
	 * @param model
	 * @return
	 */
	// @RequiresPermissions("product:add:view")
	@ApiOperation(value = "添加页面跳转", httpMethod = "GET", response = String.class)
	@RequestMapping(value = "add/view", method = RequestMethod.GET)
	public String getAddPage(Model model) {
		model.addAttribute("categoryList", EnumHelper.inspectConstants(EProductCagetory.class));
		return "product/product_add";
	}

	/**
	 * Description: add a product
	 *
	 * @param dto
	 * @return
	 */
	// @RequiresPermissions("product:add")
	@RequestMapping(value = "add", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "添加", httpMethod = "POST", response = ResultDto.class)
	public ResultDto add(@ApiParam(required = true, value = "dto") @RequestBody @OnValid(value = { CreateProduct.class,
			Default.class }) ProductReqDto dto) {
		dto.setId(null);
		productService.saveProduct(dto);
		return ResultDtoFactory
				.toRedirect(WebUtil.getFullUrlBasedOn(ApplicationConstant.GLOBAL_CONTEXT + "product/view"));
	}

	/**
	 * Description: render the detail page of a product
	 *
	 * @param id
	 * @param model
	 * @return
	 */
	// @RequiresPermissions("product:detatil")
	@RequestMapping(value = "detatil", method = RequestMethod.GET)
	@ApiOperation(value = "详情页面跳转", httpMethod = "GET", response = String.class)
	public String detail(@ApiParam(required = true, value = "编号") @RequestParam(value = "id") String id, Model model) {
		ProductPo detail = productService.findProductById(id);
		if (detail == null) {
			throw new BizServiceException(EErrorCode.PRODUCT_NOT_FOUND);
		}
		model.addAttribute("detail", detail);
		model.addAttribute("categoryList", EnumHelper.inspectConstants(EProductCagetory.class));
		return "product/product_edit";
	}

	/**
	 * Description: update a product
	 *
	 * @param id
	 * @param dto
	 * @return
	 */
	// @RequiresPermissions("product:update")
	@RequestMapping(value = "update", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "更新", httpMethod = "POST", response = ResultDto.class)
	public ResultDto update(@ApiParam(required = true, value = "dto") @RequestBody @OnValid ProductReqDto dto) {
		productService.saveProduct(dto);
		return ResultDtoFactory.toAck(getMessage(COMMON_SAVE_SUCCESS));
	}

	/**
	 * Description: delete a product
	 *
	 * @param id
	 * @return
	 */
	// @RequiresPermissions("product:delete")
	@RequestMapping(value = "delete", method = RequestMethod.POST)
	@ResponseBody
	@ApiOperation(value = "删除", httpMethod = "POST", response = ResultDto.class)
	public ResultDto delete(@ApiParam(required = true, value = "编号") @RequestParam("id") String id) {
		// id = null;
		try {
			productService.deleteProduct(id);
		} catch (BizServiceException ex) {
			return ResultDtoFactory.toNack(ex.getError());
		}
		return ResultDtoFactory.toAck(getMessage(COMMON_REMOVE_SUCCESS));
	}

	private List<EnumOption> getProductCagetory() {
		List<EnumOption> options = new ArrayList<EnumOption>();
		options.add(new EnumOption(EProductCagetory.BIRDS.getCode(), EProductCagetory.BIRDS.getText()));
		options.add(new EnumOption(EProductCagetory.CATS.getCode(), EProductCagetory.CATS.getText()));
		options.add(new EnumOption(EProductCagetory.DOGS.getCode(), EProductCagetory.DOGS.getText()));
		return options;
	}

}