package controllers.tag;

import java.util.ArrayList;
import java.util.List;

import models.Secao;

public class NoSecao {
	
	private Secao noPai = null;
	private Secao secao;
	private List<NoSecao> filhos;
	
	public NoSecao(Secao pai, Secao no) {
		this.noPai = pai;
		this.secao = no;
	}
	
	public NoSecao() {
	}
	
	public Boolean hasNoPai() {
		return this.noPai==null;
	}
	
	public Secao getNoPai() {
		return this.noPai;
	}
	
	public List<NoSecao> getFilhos() {
		if(this.filhos==null)
			this.filhos = new ArrayList<NoSecao>();
		
		return this.filhos;
	}
	
	public void setSecao(Secao secao) {
		if(secao!=null)
			this.secao = secao;
	}
	
	public void addFilho(NoSecao no) {
		if(no!=null) {
			no.setNoPai(this.getSecao());
			
			getFilhos().add(no);
		}
	}

	/**
	 * @return the secao
	 */
	public Secao getSecao() {
		return secao;
	}

	/**
	 * @param noPai the noPai to set
	 */
	public void setNoPai(Secao noPai) {
		this.noPai = noPai;
	}
	
	public String buildMenu() {
		StringBuilder _menu = new StringBuilder();
		
		_menu.append("<li>");
		
		if(!this.hasNoPai())
			_menu.append(	"<a class=\"subMenusContainer\" href=\"/produtos/secao/").append(this.secao.id).append("/").append(secao.getDescricao()).append("\">").append(secao.getDescricao()).append("</a>");
		else
			_menu.append(	"<a href=\"#").append("\">").append(secao.getDescricao()).append("</a>");
		
		buildChilds(_menu, getFilhos());
		
		_menu.append("</li>");
		
		return _menu.toString();
	}
	
	public String buildFootMenu() {
		StringBuilder _menu = new StringBuilder();
		
		if(!this.hasNoPai()) {
			_menu.append(	"<a class=\"a1\" href=\"/produtos/secao/").append(this.secao.id).append("/").append(secao.getDescricao()).append("\">")
			.append(secao.getDescricao())
			.append("</a><br />");
		}else {
			_menu.append(	"<div style=\"float:left; width: 23%;padding: 5px;\"><span class=\"fontGreen2").append("\">")
			.append(secao.getDescricao())
			.append("</span><br />");
		}
		buildFootChilds(_menu, getFilhos());
		_menu.append("</div>");
		
		return _menu.toString();
	}
	
	private void buildFootChilds(StringBuilder _menu, List<NoSecao> filhos) {
		if(!filhos.isEmpty()) {
			for(NoSecao _no : filhos) {
				if(!_no.hasNoPai())
					_menu.append("<a class=\"a1\" href=\"/produtos/secao/").append(_no.getSecao().id).append("/").append(_no.getSecao().getDescricao()).append("\">").append(_no.getSecao().getDescricao()).append("</a><br />");
				else
					_menu.append(	"<div style=\"float:left\"><span class=\"fontGreen2").append("\">").append(_no.getSecao().getDescricao()).append("</span><br />");
			
				buildFootChilds(_menu, _no.getFilhos());
			}
		}
	}
	
	private void buildChilds(StringBuilder _menu, List<NoSecao> filhos) {
		if(!filhos.isEmpty()) {
			_menu.append("<ul>");
			
			for(NoSecao _no : filhos) {
				if(!_no.hasNoPai())
					_menu.append("<li>").append("<a class=\"subMenusContainer\" href=\"/produtos/secao/").append(_no.getSecao().id).append("/").append(_no.getSecao().getDescricao()).append("\">").append(_no.getSecao().getDescricao()).append("</a>");
				else
					_menu.append(	"<a href=\"#").append("\">").append(_no.getSecao().getDescricao()).append("</a>");
			
				buildChilds(_menu, _no.getFilhos());
				
				_menu.append("</li>");
			}
			_menu.append("</ul>");
		}
	}

}
