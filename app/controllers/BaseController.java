/**
 * 
 */
package controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import models.Endereco.UF;

import play.Logger;
import play.mvc.Catch;
import play.mvc.Controller;

/**
 * @author guerrafe
 *
 */
public class BaseController extends Controller {
	
	@Catch(value=Throwable.class, priority=1)
	public static void trataException(Throwable e) {
		Logger.error(e, "Ocorreu um erro na execução!");
		
		Mail.sendError("Erro no Site", Mail.EMAIL_ADMIN, Mail.EMAIL_ADMIN, new Exception(e));
	}
	
	protected static List<String> getListaUF() {
		List<String> list = new ArrayList<String>();
		
		list.add("");
		
		for(UF uf : UF.values())
			list.add(uf.getSiglaUF());
		
		Collections.sort(list);
		
		return list;
	}

}
