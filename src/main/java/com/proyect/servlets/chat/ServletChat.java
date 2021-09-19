package com.proyect.servlets.chat;

import com.proyect.chat.daos.*;
import com.proyect.chat.model.Message;
import com.proyect.chat.model.Speaker;
import com.proyect.chat.notify.ComplexNotification;
import jakarta.json.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@WebServlet(name = "ServletChat", value = "/ServletChat")
public class ServletChat extends HttpServlet {

  //objects for actions in mongoDB
  private final MessageDao messageDao = new MessageDao();

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    //TODO:get info from javaScript
    String idEmitter = request.getParameter("emitter");
    String idReceiver = request.getParameter("receiver");

    if (idEmitter != null && idReceiver != null) {
      //Method for send information of speaker to client
      Speaker emitter = new Speaker().setId(new ObjectId(idEmitter));
      Speaker receiver = new Speaker().setId(new ObjectId(idReceiver));

      List<Message> conversation = messageDao.getConversation(emitter, receiver);

      PrintWriter out = response.getWriter();
      response.setContentType("application/json");
      response.setCharacterEncoding("UTF-8");
      out.print(parseToJson(conversation).toString());
      out.flush();

    } else {
      //redirect to message.jsp and send the list of speakers
      request.getSession().setAttribute("speakers", new ComplexNotification().buildNotification());
      request.getRequestDispatcher("/views/admin/message.jsp").forward(request, response);
    }
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

  }

  /***
   * This method converter a message list to JsonArray for send to client JS
   * @param messages Message List that comming from messageDao
   * @return a JsonArray ready to ship to client JS
   */
  private JsonArray parseToJson(List<Message> messages) {

    Map<String, String> config = new TreeMap<>();
    JsonBuilderFactory factory = Json.createBuilderFactory(config);

    JsonArrayBuilder jsonBuilder = factory.createArrayBuilder();

    messages.forEach(message -> jsonBuilder.add(factory.createObjectBuilder()
           .add("emitterIsEmployee", message.getEmitter().isEmployee())
           .add("content", message.getContent())
    ));

    return jsonBuilder.build();
  }
}
