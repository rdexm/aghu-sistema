package br.gov.mec.aghu.compras.contaspagar.action;

import javax.annotation.PostConstruct;

import br.gov.mec.aghu.compras.contaspagar.vo.PosicaoTituloVO;
import br.gov.mec.aghu.core.action.ActionController;

/**
 * #37797 – Consultar posição do Título
 * 
 * @author aghu
 *
 */
public class ConsultaPosicaoTituloController extends ActionController {

	private static final long serialVersionUID = 3800683905605563982L;

	private PosicaoTituloVO vo;

	private String voltarPara;

	@PostConstruct
	protected void inicializar() {
		begin(this.conversation);
	}

	public String voltar() {
		String retorno = this.voltarPara;
		this.vo = null;
		this.voltarPara = null;
		return retorno;
	}

	/*
	 * Getters/Setters
	 */
	public PosicaoTituloVO getVo() {
		return vo;
	}

	public void setVo(PosicaoTituloVO vo) {
		this.vo = vo;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

}