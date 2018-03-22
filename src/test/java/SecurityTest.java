import com.github.fnpac.config.WebApplicationConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * Created by 刘春龙 on 2018/3/9.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(
        classes = {
                WebApplicationConfig.class
        })
public class SecurityTest {

    private static final Logger logger = LoggerFactory.getLogger(SecurityTest.class);

    @Test
    public void passwordEncoder() {

        StandardPasswordEncoder encoder = new StandardPasswordEncoder("53cr3t");
        String password = encoder.encode("qwe521314");
//        Password:7ec31970b467ac561d42038515c7323f7ca29feac596553ad6174948d4e32f005e8569aaff18980d
        logger.info("Password:" + password);
    }
}
