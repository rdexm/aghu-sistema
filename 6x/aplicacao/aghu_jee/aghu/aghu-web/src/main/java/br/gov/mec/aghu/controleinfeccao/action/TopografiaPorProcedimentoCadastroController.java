package br.gov.mec.aghu.controleinfeccao.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.controleinfeccao.business.IControleInfeccaoFacade;
import br.gov.mec.aghu.controleinfeccao.vo.TopografiaInfeccaoVO;
import br.gov.mec.aghu.controleinfeccao.vo.TopografiaProcedimentoVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class TopografiaPorProcedimentoCadastroController extends ActionController {

	private static final long serialVersionUID = 2879105568647544309L;

	@EJB
	private IControleInfeccaoFacade controleInfeccaoFacade;

	private TopografiaProcedimentoVO vo;
	private String voltarPara;
	private boolean edicao;
	private TopografiaInfeccaoVO topografiaInfeccaoVO;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void inicio() {
	 

	 

		if(vo == null){
			vo =  new  TopografiaProcedimentoVO();
			vo.setSituacaoBoolean(Boolean.TRUE);
		}else{
			edicao = true;
			topografiaInfeccaoVO = suggestionBoxTopografiaInfeccaoPorSituacao(vo.getSeqTopografiaInfeccao().toString()).get(0);
		}
	
	}
	

	public String gravar() {
		try {
			vo.setSeqTopografiaInfeccao(topografiaInfeccaoVO != null ? topografiaInfeccaoVO.getSeq() : null);
			controleInfeccaoFacade.persistirTopografiaProcedimento(vo);

			if (isEdicao()) {
				apresentarMsgNegocio(Severity.INFO, "MSG_TOPO_PROC_SUCESSO_ALTERACAO", vo.getDescricao());
			} else {
				apresentarMsgNegocio(Severity.INFO, "MSG_TOPO_PROC_SUCESSO_CADASTRO", vo.getDescricao());
			}

			limpar();
			return getVoltarPara();

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}
	
	public List<TopografiaInfeccaoVO> suggestionBoxTopografiaInfeccaoPorSituacao(String strPesquisa) {
		return this.controleInfeccaoFacade.suggestionBoxTopografiaInfeccaoPorSeqOuDescricao((String) strPesquisa);
	}
	
	public String cancelar() {
		limpar();
		return getVoltarPara();
	}
	
	private void limpar(){
		vo = null;
		edicao = false;
		topografiaInfeccaoVO = null;
	}

	public TopografiaProcedimentoVO getVo() {
		return vo;
	}

	public void setVo(TopografiaProcedimentoVO vo) {
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

	public TopografiaInfeccaoVO getTopografiaInfeccaoVO() {
		return topografiaInfeccaoVO;
	}

	public void setTopografiaInfeccaoVO(
			TopografiaInfeccaoVO topografiaInfeccaoVO) {
		this.topografiaInfeccaoVO = topografiaInfeccaoVO;
	}
	
}
