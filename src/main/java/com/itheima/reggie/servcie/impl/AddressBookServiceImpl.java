package com.itheima.reggie.servcie.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.entity.AddressBook;
import com.itheima.reggie.mapper.AddressBookMapper;
import com.itheima.reggie.servcie.AddressBookService;
import org.springframework.stereotype.Service;

/**
 * @author 梁志超
 * @version 1.0
 * @time 2023/4/16 10:28
 */
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
