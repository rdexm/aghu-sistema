package br.gov.mec.casca.app.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;

import br.gov.mec.casca.app.dao.AplicacaoDAO;
import br.gov.mec.casca.app.menu.vo.AplicacaoVO;
import br.gov.mec.casca.model.Aplicacao;

/**
 * Componente que guarda as aplicações cadastradas.
 * 
 * @author manoelsantos
 * @version 1.0
 */
@Scope(ScopeType.SESSION)
@Name("aplicacaoBar")
public class AplicacaoBar {
	private static final long serialVersionUID = -7552889471435717713L;
	private List<Aplicacao> aplicacaoBar = new ArrayList<Aplicacao>();
	private Map<Integer, AplicacaoVO> vos = new HashMap<Integer, AplicacaoVO>(); 

	@Logger
	private Log log;

	/**
	 * Carrega os dados das aplicações.
	 * 
	 * @return
	 */
	public List<Aplicacao> carregarAplicacoes() {
		if (aplicacaoBar.isEmpty()) {
			log.info("Carregando aplicações...");
			aplicacaoBar.addAll(new AplicacaoDAO().recuperarAplicacoes());
		}

		return aplicacaoBar;
	}

	/**
	 * @return the vos
	 */
	public Map<Integer, AplicacaoVO> getVos() {
		return vos;
	}

	/**
	 * @param vos the vos to set
	 */
	public void setVos(Map<Integer, AplicacaoVO> vos) {
		this.vos = vos;
	}
}