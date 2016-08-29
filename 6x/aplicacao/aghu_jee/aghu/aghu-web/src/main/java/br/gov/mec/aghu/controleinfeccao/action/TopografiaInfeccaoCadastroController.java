package br.gov.mec.aghu.controleinfeccao.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.controleinfeccao.business.IControleInfeccaoFacade;
import br.gov.mec.aghu.controleinfeccao.vo.TopografiaInfeccaoVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class TopografiaInfeccaoCadastroController extends ActionController {

	private static final long serialVersionUID = 2879105568647544309L;

	@EJB
	private IControleInfeccaoFacade controleInfeccaoFacade;

	private TopografiaInfeccaoVO vo;
	private String voltarPara;
	private boolean edicao;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void inicio() {
	 

	 

		if(vo == null){
			vo =  new  TopografiaInfeccaoVO();
			vo.setSituacaoBoolean(Boolean.TRUE);
		}else{
			edicao = true;
		}
	
	}
	

	public String gravar() {
		try {
			controleInfeccaoFacade.persistirTopografiaInfeccao(vo);

			if (isEdicao()) {
				apresentarMsgNegocio(Severity.INFO, "MSG_TOPO_INFE_SUCESSO_ALTERACAO", vo.getDescricao());

			} else {
				apresentarMsgNegocio(Severity.INFO, "MSG_TOPO_INFE_SUCESSO_CADASTRO", vo.getDescricao());
			}

			limpar();
			return getVoltarPara();

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}

	public String cancelar() {
		limpar();
		return getVoltarPara();
	}
	
	private void limpar(){
		vo = null;
		edicao = false;
	}

	public TopografiaInfeccaoVO getVo() {
		return vo;
	}

	public void setVo(TopografiaInfeccaoVO vo) {
		this.vo = vo;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public boolean isEdicao() {
		return edicao;
	}

	public void setEdicao(boolean edicao) {
		this.edicao = edicao;
	}
	
}
