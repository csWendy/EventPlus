package rpc;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;


import entity.Item;
import external.TicketMasterClient;



/**
 * Servlet implementation class SearchItem
 */
@WebServlet("/search")
public class SearchItem extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public SearchItem() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		if (session == null) {
			response.setStatus(403);
			return;
		}
		
		String userId = session.getAttribute("user_id").toString();

		double lat = Double.parseDouble(request.getParameter("lat"));
		double lon = Double.parseDouble(request.getParameter("lon"));
		
		// Term can be empty or null.
		String term = request.getParameter("term");
		
		DBConnection connection = DBConnectionFactory.getConnection();
          try {
        	  List<Item> items = connection.searchItems(lat, lon, term);
        	  Set<String> favoritedItemIds = connection.getFavoriteItemIds(userId);
        	  JSONArray array = new JSONArray();
        	  for (Item item : items) {
        		  JSONObject obj = item.toJSONObject();
  					obj.put("favorite", favoritedItemIds.contains(item.getItemId()));
  					array.put(obj);

        	  }
        	  RpcHelper.writeJsonArray(response, array);

          } catch (Exception e) {
        	  e.printStackTrace();
         } finally {
        	 connection.close();
         }


		
		TicketMasterClient client = new TicketMasterClient();
		List<Item> items = client.search(lat, lon, null);
		JSONArray array = new JSONArray();
		for (Item item : items) {
			array.put(item.toJSONObject());
		}
		RpcHelper.writeJsonArray(response, array);
//		
//		// TODO Auto-generated method stub
//		//response.getWriter().append("Served at: ").append(request.getContextPath());
//		
//		response.setContentType("application/json");//tell client the content of response.
//		PrintWriter writer = response.getWriter();
//		
//		
//		JSONArray array = new JSONArray();
//		
//		try{
//			array.put(new JSONObject().put("username", "abcd").put("address", "San Francisco").put("time", "01/01/2017"));
//			array.put(new JSONObject().put("username", "1234").put("address", "San Jose").put("time", "01/02/2017"));
//
//
//		}catch (JSONException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();	
//		}
//			

		//RpcHelper.writeJsonArray(response, array);	
		
		//writer.close();//release the resource writer used. improve performance.
		//java will close it automatically
		//in real, backend will return json string, front-end will display the json.
		
		}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
