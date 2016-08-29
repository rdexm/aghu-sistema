package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.agendamento.business.IAgendamentoExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.exames.vo.AelAgrpPesquisaXExameVO;
import br.gov.mec.aghu.model.AelAgrpPesquisaXExame;
import br.gov.mec.aghu.model.AelAgrpPesquisas;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AelUnfExecutaExamesId;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class GrupoPesquisaExameController extends ActionController {

	
	private static final long serialVersionUID = 1437561463545536532L;

	private static final String GRUPO_PESQUISA_EXAME_LIST = "grupoPesquisaExameList";

	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	@EJB
	private IAgendamentoExamesFacade agendamentoExamesFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	private AelAgrpPesquisas aelAgrpPesquisas;
	private List<AelAgrpPesquisaXExameVO> listaAelAgrpPesquisaXExameVO;
	private AelAgrpPesquisaXExameVO unfExecutaExameVO;
	
	private Short seq;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String iniciar() {
	 

		unfExecutaExameVO = null;
		
		if (aelAgrpPesquisas != null && aelAgrpPesquisas.getSeq() != null) {
			this.aelAgrpPesquisas = this.cadastrosApoioExamesFacade.obterAelAgrpPesquisasPorId(aelAgrpPesquisas.getSeq()); 
			this.listaAelAgrpPesquisaXExameVO = this.cadastrosApoioExamesFacade.obterAelAgrpPesquisaXExamePorAelAgrpPesquisas(aelAgrpPesquisas, null, true);
		} else {
			this.aelAgrpPesquisas = new AelAgrpPesquisas();
		}
		
		return null;
	
	}
	
	/**
	 * Confirma a operacao de gravar/alterar um grupo de exame
	 */
	public String confirmar() {
		try{
			
			this.cadastrosApoioExamesFacade.persistirAelAgrpPesquisas(aelAgrpPesquisas);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_GRUPO_PESQUISA_EXAME",aelAgrpPesquisas.getDescricao());
			
			return voltar();
			
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
		
		return null;
	}
	
	public String voltar(){
		seq = null;
		aelAgrpPesquisas = null;
		return GRUPO_PESQUISA_EXAME_LIST;
	}
	
	public void confirmarAelAgrpPesquisaXExame(){

		try{
			
			final AelAgrpPesquisaXExame reg = new AelAgrpPesquisaXExame();
			reg.setAgrpPesquisa(aelAgrpPesquisas);
			
			final AghUnidadesFuncionais unf = aghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(unfExecutaExameVO.getUnfSeq()); 
			AelUnfExecutaExames aa = agendamentoExamesFacade.obterAelUnfExecutaExamesDAOPorId(new AelUnfExecutaExamesId( unfExecutaExameVO.getEmaExaSigla(), 
																														 unfExecutaExameVO.getEmaManSeq(),
																														 unf));
			reg.setUnfExecutaExame(aa);
			reg.setIndSituacao(DominioSituacao.A);
			
			this.cadastrosApoioExamesFacade.persistirAelAgrpPesquisaXExame(reg);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_GRUPOS_EXAME_X_CRIADO_SUCESSO",unfExecutaExameVO.getEmaExaSigla());
			this.listaAelAgrpPesquisaXExameVO = this.cadastrosApoioExamesFacade.obterAelAgrpPesquisaXExamePorAelAgrpPesquisas(aelAgrpPesquisas, null, true);
			unfExecutaExameVO = null;
			
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}

	public void ativarInativar(final AelAgrpPesquisaXExameVO vo) {
		try {
			AelAgrpPesquisaXExame aelAgrpPesquisaXExame = cadastrosApoioExamesFacade.obterAelAgrpPesquisaXExamePorId(vo.getSeq());
			aelAgrpPesquisaXExame.setIndSituacao( (DominioSituacao.A.equals(aelAgrpPesquisaXExame.getIndSituacao()) ? DominioSituacao.I : DominioSituacao.A) );
			cadastrosApoioExamesFacade.persistirAelAgrpPesquisaXExame(aelAgrpPesquisaXExame);
			this.apresentarMsgNegocio( Severity.INFO, ( DominioSituacao.A.equals(aelAgrpPesquisaXExame.getIndSituacao()) 
												    	? "MENSAGEM_GRUPOS_EXAME_X_INATIVADO_SUCESSO" 
														: "MENSAGEM_GRUPOS_EXAME_X_ATIVADO_SUCESSO"), aelAgrpPesquisaXExame.getSeq());
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		
		this.listaAelAgrpPesquisaXExameVO = this.cadastrosApoioExamesFacade.obterAelAgrpPesquisaXExamePorAelAgrpPesquisas(aelAgrpPesquisas,null,true);
	}
	
	public List<AelAgrpPesquisaXExameVO> suggestionAelAgrpPesquisaXExameVO(String filtro){
		return this.cadastrosApoioExamesFacade.obterAelAgrpPesquisaXExamePorAelAgrpPesquisas(aelAgrpPesquisas,(String) filtro, false);
	}

	public List<AelAgrpPesquisaXExameVO> getListaAelAgrpPesquisaXExameVO() {
		return listaAelAgrpPesquisaXExameVO;
	}

	public void setListaAelAgrpPesquisaXExameVO(
			List<AelAgrpPesquisaXExameVO> listaAelAgrpPesquisaXExameVO) {
		this.listaAelAgrpPesquisaXExameVO = listaAelAgrpPesquisaXExameVO;
	}

	public Short getSeq() {
		return seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	public AelAgrpPesquisas getAelAgrpPesquisas() {
		return aelAgrpPesquisas;
	}

	public void setAelAgrpPesquisas(AelAgrpPesquisas aelAgrpPesquisas) {
		this.aelAgrpPesquisas = aelAgrpPesquisas;
	}

	public AelAgrpPesquisaXExameVO getUnfExecutaExameVO() {
		return unfExecutaExameVO;
	}

	public void setUnfExecutaExameVO(AelAgrpPesquisaXExameVO unfExecutaExameVO) {
		this.unfExecutaExameVO = unfExecutaExameVO;
	}
}