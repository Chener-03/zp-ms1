package xyz.chener.zp.zpusermodule;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.util.TablesNamesFinder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import xyz.chener.zp.common.config.query.CustomFieldQuery;
import xyz.chener.zp.zpusermodule.entity.UserBase;
import xyz.chener.zp.zpusermodule.entity.dto.UserAllInfoDto;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
public class UserApplication {

    public static void main(String[] args) throws Exception {
        String sql = "select a.qq as qqq from f1 a,f2 b where a.id=b.id";

        Select select = (Select) CCJSqlParserUtil.parse(sql);
        PlainSelect selectBody = (PlainSelect) select.getSelectBody();
        List<Join> joins = selectBody.getJoins();
        FromItem fromItem = selectBody.getFromItem();
        List<SelectItem> selectItems = selectBody.getSelectItems();

        selectBody.getFromItem().accept(new FromItemVisitorAdapter() {
            @Override
            public void visit(Table table) {
                System.out.println(table.getName());
            }
        });

        selectBody.getSelectItems().get(0).accept(new SelectItemVisitorAdapter() {
            @Override
            public void visit(SelectExpressionItem item) {
                System.out.println(item.getExpression());
            }
        });

        selectBody.getJoins().get(0).getRightItem().accept(new FromItemVisitorAdapter() {
            @Override
            public void visit(Table table) {
                System.out.println(table.getName());
            }
        });
;


        System.setProperty("csp.sentinel.log.output.type","console");
        SpringApplication.run(UserApplication.class, args);
    }

}
