package com.iwhalecloud.consumercenter.mvc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.iwhalecloud.consumercenter.common.result.PageResult;
import com.iwhalecloud.consumercenter.mvc.mapper.CustomerMapper;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.customer.CustomerCreateDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.customer.CustomerExportDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.customer.CustomerQueryDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.customer.CustomerStatusUpdateDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.dto.customer.CustomerUpdateDTO;
import com.iwhalecloud.consumercenter.mvc.pojo.convert.CustomerConvert;
import com.iwhalecloud.consumercenter.mvc.pojo.entity.Customer;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.customer.CustomerDetailVO;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.customer.CustomerExportVO;
import com.iwhalecloud.consumercenter.mvc.pojo.vo.customer.CustomerVO;
import com.iwhalecloud.consumercenter.mvc.service.CustomerService;
import com.iwhalecloud.consumercenter.mvc.service.OperationLogService;
import lombok.extern.slf4j.Slf4j;
import org.apache.fesod.sheet.FesodSheet;
import org.apache.fesod.sheet.write.handler.SheetWriteHandler;
import org.apache.fesod.sheet.write.handler.context.SheetWriteHandlerContext;
import org.apache.fesod.sheet.write.metadata.style.WriteCellStyle;
import org.apache.fesod.sheet.write.metadata.style.WriteFont;
import org.apache.fesod.sheet.write.style.HorizontalCellStyleStrategy;
import org.apache.fesod.sheet.write.style.column.SimpleColumnWidthStyleStrategy;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 客户服务实现
 */
@Slf4j
@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private OperationLogService operationLogService;

    private static final List<String> CHINESE_HEADERS = List.of("客户编号", "客户名称", "行业", "区域", "规模", "状态", "客户经理");
    private static final List<String> ENGLISH_HEADERS = List.of("Customer Code", "Customer Name", "Industry", "Region", "Scale", "Status", "Manager");
    private static final int COLUMN_WIDTH = 18;
    private static final SheetWriteHandler FREEZE_HEADER_HANDLER = new SheetWriteHandler() {
        @Override
        public void afterSheetCreate(SheetWriteHandlerContext context) {
            context.getWriteSheetHolder().getSheet().createFreezePane(0, 1);
        }
    };

    @Override
    public PageResult<CustomerVO> pageList(CustomerQueryDTO queryDTO) {
        Page<Customer> page = new Page<>(queryDTO.getPage(), queryDTO.getSize());
        Page<Customer> result = customerMapper.selectPage(page, buildQueryWrapper(queryDTO));

        List<CustomerVO> voList = result.getRecords().stream()
                .map(CustomerConvert::entity2vo)
                .collect(Collectors.toList());

        PageResult<CustomerVO> pageResult = new PageResult<>();
        pageResult.setTotal(result.getTotal());
        pageResult.setPages(result.getPages());
        pageResult.setCurrent(result.getCurrent());
        pageResult.setSize(result.getSize());
        pageResult.setRecords(voList);

        return pageResult;
    }

    @Override
    public CustomerExportVO export(CustomerExportDTO exportDTO) {
        List<Customer> customers = customerMapper.selectList(buildQueryWrapper(exportDTO));
        boolean english = isEnglish(exportDTO.getLang());

        CustomerExportVO exportVO = new CustomerExportVO();
        exportVO.setTotal(customers.size());
        exportVO.setFilename(buildExportFilename(english));
        if (customers.isEmpty()) {
            exportVO.setContent(new byte[0]);
            return exportVO;
        }

        List<String> headers = english ? ENGLISH_HEADERS : CHINESE_HEADERS;
        List<List<String>> head = headers.stream().map(header -> List.of(header)).collect(Collectors.toList());
        List<List<Object>> rows = customers.stream()
                .map(CustomerConvert::entity2vo)
                .map(vo -> List.<Object>of(
                        defaultString(vo.getCustomerCode()),
                        defaultString(vo.getCustomerName()),
                        defaultString(vo.getIndustry()),
                        defaultString(vo.getRegion()),
                        defaultString(vo.getScale()),
                        defaultString(vo.getStatusDesc()),
                        defaultString(vo.getManagerName())))
                .collect(Collectors.toList());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        FesodSheet.write(outputStream)
                .head(head)
                .registerWriteHandler(buildHeaderStyleStrategy())
                .registerWriteHandler(new SimpleColumnWidthStyleStrategy(COLUMN_WIDTH))
                .registerWriteHandler(FREEZE_HEADER_HANDLER)
                .sheet(english ? "Customer List" : "客户列表")
                .doWrite(rows);

        exportVO.setContent(outputStream.toByteArray());
        return exportVO;
    }

    private LambdaQueryWrapper<Customer> buildQueryWrapper(CustomerQueryDTO queryDTO) {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();

        // 构建查询条件
        if (StringUtils.hasText(queryDTO.getKeyword())) {
            wrapper.and(w -> w.like(Customer::getCustomerName, queryDTO.getKeyword())
                    .or().like(Customer::getCustomerCode, queryDTO.getKeyword()));
        }
        if (StringUtils.hasText(queryDTO.getIndustry())) {
            wrapper.eq(Customer::getIndustry, queryDTO.getIndustry());
        }
        if (StringUtils.hasText(queryDTO.getRegion())) {
            wrapper.eq(Customer::getRegion, queryDTO.getRegion());
        }
        if (StringUtils.hasText(queryDTO.getScale())) {
            wrapper.eq(Customer::getScale, queryDTO.getScale());
        }
        if (queryDTO.getStatus() != null) {
            wrapper.eq(Customer::getStatus, queryDTO.getStatus());
        }
        if (StringUtils.hasText(queryDTO.getSource())) {
            wrapper.eq(Customer::getSource, queryDTO.getSource());
        }

        wrapper.orderByDesc(Customer::getCreateTime);
        return wrapper;
    }

    private boolean isEnglish(String lang) {
        return lang != null && lang.toLowerCase(Locale.ROOT).startsWith("en");
    }

    private String buildExportFilename(boolean english) {
        String prefix = english ? "customer-list" : "客户列表";
        return prefix + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx";
    }

    private String defaultString(String value) {
        return value == null ? "" : value;
    }

    private HorizontalCellStyleStrategy buildHeaderStyleStrategy() {
        WriteCellStyle headerStyle = new WriteCellStyle();
        headerStyle.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        WriteFont headerFont = new WriteFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 11);
        headerStyle.setWriteFont(headerFont);
        return new HorizontalCellStyleStrategy(headerStyle, new WriteCellStyle());
    }

    @Override
    public CustomerDetailVO getDetail(Long id) {
        Customer customer = customerMapper.selectById(id);
        if (customer == null) {
            throw new RuntimeException("客户不存在");
        }

        CustomerDetailVO detailVO = CustomerConvert.entity2detailVO(customer);
        // Demo阶段设置模拟数据
        detailVO.setBranchCount(3);
        detailVO.setDecisionMakerCount(2);
        detailVO.setInteractionCount(5);

        return detailVO;
    }

    @Override
    public CustomerVO create(CustomerCreateDTO createDTO) {
        Customer customer = CustomerConvert.dto2entity(createDTO);

        // 生成客户编号
        customer.setCustomerCode(generateCustomerCode());
        customer.setStatus((byte) 0); // 默认未看管
        customer.setJoinTime(LocalDateTime.now());
        customer.setSource("手动录入");

        customerMapper.insert(customer);

        operationLogService.log(customer.getId(), "customer", "CREATE",
                "新增客户: " + customer.getCustomerName());

        return CustomerConvert.entity2vo(customer);
    }

    @Override
    public CustomerVO update(CustomerUpdateDTO updateDTO) {
        Customer customer = customerMapper.selectById(updateDTO.getId());
        if (customer == null) {
            throw new RuntimeException("客户不存在");
        }

        String beforeData = customer.toString();
        CustomerConvert.dto2entity(updateDTO, customer);
        customerMapper.updateById(customer);

        operationLogService.log(customer.getId(), "customer", "UPDATE",
                "修改客户: " + customer.getCustomerName(), beforeData, customer.toString());

        return CustomerConvert.entity2vo(customer);
    }

    @Override
    public void delete(Long id) {
        Customer customer = customerMapper.selectById(id);
        if (customer == null) {
            throw new RuntimeException("客户不存在");
        }

        customerMapper.deleteById(id);

        operationLogService.log(id, "customer", "DELETE",
                "删除客户: " + customer.getCustomerName());
    }

    @Override
    public CustomerVO updateStatus(Long id, CustomerStatusUpdateDTO statusDTO) {
        Customer customer = customerMapper.selectById(id);
        if (customer == null) {
            throw new RuntimeException("客户不存在");
        }

        Byte oldStatus = customer.getStatus();
        customer.setStatus(statusDTO.getStatus() != null ? statusDTO.getStatus().byteValue() : null);
        customerMapper.updateById(customer);

        String statusDesc = getStatusDesc(statusDTO.getStatus() != null ? statusDTO.getStatus().byteValue() : null);
        operationLogService.log(id, "customer", "STATUS_CHANGE",
                "客户状态变更: " + getStatusDesc(oldStatus) + " -> " + statusDesc
                        + (statusDTO.getReason() != null ? ", 原因: " + statusDTO.getReason() : ""));

        return CustomerConvert.entity2vo(customer);
    }

    private String getStatusDesc(Byte status) {
        if (status == null) return "未知";
        return switch (status) {
            case 0 -> "未看管";
            case 1 -> "已看管";
            case 2 -> "已剔除";
            default -> "未知";
        };
    }

    private String generateCustomerCode() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "CUS" + timestamp + random;
    }
}
