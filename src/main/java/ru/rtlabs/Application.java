package ru.rtlabs;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.rtlabs.Service.Service;
import java.io.IOException;

public class Application {
    private static final Logger LOG =Logger.getLogger(Application.class);

    public static void main(String[] args) throws IOException {
        String[] actions = {"get_single_inserted_branch_by_id", "get_inserted_divisions_of_clinic",
                "get_inserted_liks_division_to_bed", "get_inserted_liks_division_to_bed", "get_inserted_amound_bed"};
        ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
        Connection connection = ctx.getBean(ConnectionFactory.class).newConnection();
        Channel channel = connection.createChannel();
        Service service = (Service) ctx.getBean("service");
        channel.queueDeclare(service.getQueue(), true, false, false, null);
        for (String s : actions) {
            service.postSend(args[0], s, channel);
        }
        channel.close();
        connection.close();
    }
}
