import com.housekeeping.admin.entity.User;
import com.housekeeping.gateway.HousekeepingGatewayApplication;
import com.housekeeping.gateway.client.UserClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = HousekeepingGatewayApplication.class)
public class CategoryClientTest {

    @Autowired
    private UserClient userClient;

    @Test
    public void testQueryCategories() {
        User users = userClient.getUserByEmail("18995636120@163.com");
        System.out.println(users.toString());
    }
}