package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.blococirurgico.procdiagterap.business.IBlocoCirurgicoProcDiagTerapFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.PdtCidPorProc;
import br.gov.mec.aghu.model.PdtComplementoPorCid;
import br.gov.mec.aghu.model.PdtProcDiagTerap;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class CidsProcedimentoComplementoCRUDController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final long serialVersionUID = -9080940970337693983L;

	private static final String CIDS_PROC_LIST = "cidsProcedimentoList";
	private static final String CIDS_PROC_CRUD = "cidsProcedimentoComplementoCRUD"; 
	
	@EJB
	private IBlocoCirurgicoProcDiagTerapFacade blocoCirurgicoProcDiagTerapFacade;

	@EJB
	private IAghuFacade aghuFacade;

	private Integer cidSeq;
	private Integer dptSeq;
	private Boolean ativoCidProcedimento;
	private AghCid cid;
	private PdtProcDiagTerap procedimento;
	private Boolean exibirSuggestionCidProcedimento;
	private PdtCidPorProc cidProcedimento;
	private Boolean exibirComplemento;
	private String descricaoComplemento;
	private Boolean ativoComplemento;
	private List<PdtComplementoPorCid> complementos;
	private PdtComplementoPorCid complemento;

	public void inicio() {
		complemento = new PdtComplementoPorCid();
		ativoComplemento = Boolean.TRUE;
		descricaoComplemento = null;
		complementos = null;
		if (cidSeq != null && dptSeq != null && cidSeq > 0 && dptSeq > 0) {
			cid = aghuFacade.obterAghCidPorChavePrimaria(cidSeq);
			procedimento = blocoCirurgicoProcDiagTerapFacade.obterPdtProcDiagTerapPorChavePrimaria(dptSeq);
			cidProcedimento = blocoCirurgicoProcDiagTerapFacade.obterPdtCidPorProcPorChavePrimaria(dptSeq,cidSeq);
			if (cidProcedimento.getIndSituacao().equals(DominioSituacao.A)) {
				ativoCidProcedimento = true;
			} else {
				ativoCidProcedimento = false;
			}
			exibirSuggestionCidProcedimento = false;
			exibirComplemento = true;
			complementos = blocoCirurgicoProcDiagTerapFacade.listarPdtComplementoPorCids(dptSeq,cidSeq);
		}else if (cidSeq == null && dptSeq != null){
			procedimento = blocoCirurgicoProcDiagTerapFacade.obterPdtProcDiagTerapPorChavePrimaria(dptSeq);
			ativoCidProcedimento = true;
			exibirSuggestionCidProcedimento = true;
			cidProcedimento = new PdtCidPorProc();
		} else {
			cid = null;
			procedimento = null;
			exibirSuggestionCidProcedimento = true;
			ativoCidProcedimento = true;
			exibirComplemento = false;
			cidProcedimento = new PdtCidPorProc();
		}
	
	}
	

	public List<PdtProcDiagTerap> listarProcedimentos(String filtro) {
		return this.returnSGWithCount(this.blocoCirurgicoProcDiagTerapFacade.pesquisarPdtProcDiagTerap((String) filtro),listarProcedimentosCount(filtro));
	}
	
	public Long listarProcedimentosCount(String filtro) {
		return this.blocoCirurgicoProcDiagTerapFacade.pesquisarPdtProcDiagTerapCount((String) filtro);
	}
	
	public List<AghCid> listarCids (String filtro) {
		String pesquisa = filtro != null ? filtro : null;
		return returnSGWithCount(aghuFacade.listarAghCidPorCodigoDescricao(pesquisa,
				null, DominioSituacao.A, false, false, 0,
				100, true, AghCid.Fields.CODIGO), listarCidsCount(filtro));
	}
	
	public Long listarCidsCount(String filtro) {
		String pesquisa = filtro != null ? filtro : null;
		return aghuFacade.listarAghCidPorCodigoDescricaoCount(
				pesquisa, null, DominioSituacao.A, false,
				false);
	}

	public String voltar() {
		cidSeq = null;
		dptSeq = null;
		return CIDS_PROC_LIST;
	}
	
	public String gravarCidProcedimento() {
		try {
			if (ativoCidProcedimento) {
				cidProcedimento.setIndSituacao(DominioSituacao.A);
			} else {
				cidProcedimento.setIndSituacao(DominioSituacao.I);
			}
			String mensagemSucesso = blocoCirurgicoProcDiagTerapFacade.persistirCidProcedimento(cidProcedimento, procedimento.getSeq(), cid.getSeq());
			apresentarMsgNegocio(Severity.INFO, mensagemSucesso);
			cidSeq =  cid.getSeq();
			dptSeq = procedimento.getSeq();
			inicio();
			exibirComplemento = true;
			return CIDS_PROC_CRUD;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			exibirComplemento = false;
		}
		return null;
	}
	
	public void gravarComplemento() {
		try {
			complemento.setDescricao(descricaoComplemento);
			if (ativoComplemento) {
				complemento.setIndSituacao(DominioSituacao.A);
			} else {
				complemento.setIndSituacao(DominioSituacao.I);
			}
			cidSeq =  cid.getSeq();
			dptSeq = procedimento.getSeq();
			String mensagemSucesso = blocoCirurgicoProcDiagTerapFacade.persistirComplemento(complemento, dptSeq, cidSeq);
			apresentarMsgNegocio(Severity.INFO, mensagemSucesso);
			complementos = blocoCirurgicoProcDiagTerapFacade.listarPdtComplementoPorCids(dptSeq,cidSeq);
			complemento = new PdtComplementoPorCid();
			descricaoComplemento = null;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void ativarDesativar(PdtComplementoPorCid complemento) {
		try {
			if (complemento.getIndSituacao().isAtivo()) {
				complemento.setIndSituacao(DominioSituacao.I);
			} else {
				complemento.setIndSituacao(DominioSituacao.A);
			}
			String mensagemSucesso = blocoCirurgicoProcDiagTerapFacade.persistirComplemento(complemento, null, null);
			apresentarMsgNegocio(Severity.INFO, mensagemSucesso);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public Integer getCidSeq() {
		return cidSeq;
	}

	public void setCidSeq(Integer cidSeq) {
		this.cidSeq = cidSeq;
	}

	public Integer getDptSeq() {
		return dptSeq;
	}

	public void setDptSeq(Integer dptSeq) {
		this.dptSeq = dptSeq;
	}

	public Boolean getAtivoCidProcedimento() {
		return ativoCidProcedimento;
	}

	public void setAtivoCidProcedimento(Boolean ativoCidProcedimento) {
		this.ativoCidProcedimento = ativoCidProcedimento;
	}

	public AghCid getCid() {
		return cid;
	}

	public void setCid(AghCid cid) {
		this.cid = cid;
	}

	public PdtProcDiagTerap getProcedimento() {
		return procedimento;
	}

	public void setProcedimento(PdtProcDiagTerap procedimento) {
		this.procedimento = procedimento;
	}

	public Boolean getExibirSuggestionCidProcedimento() {
		return exibirSuggestionCidProcedimento;
	}

	public void setExibirSuggestionCidProcedimento(Boolean exibirSuggestionCidProcedimento) {
		this.exibirSuggestionCidProcedimento = exibirSuggestionCidProcedimento;
	}

	public PdtCidPorProc getCidProcedimento() {
		return cidProcedimento;
	}

	public void setCidProcedimento(PdtCidPorProc cidProcedimento) {
		this.cidProcedimento = cidProcedimento;
	}

	public Boolean getExibirComplemento() {
		return exibirComplemento;
	}

	public void setExibirComplemento(Boolean exibirComplemento) {
		this.exibirComplemento = exibirComplemento;
	}

	public String getDescricaoComplemento() {
		return descricaoComplemento;
	}

	public void setDescricaoComplemento(String descricaoComplemento) {
		this.descricaoComplemento = descricaoComplemento;
	}

	public Boolean getAtivoComplemento() {
		return ativoComplemento;
	}

	public void setAtivoComplemento(Boolean ativoComplemento) {
		this.ativoComplemento = ativoComplemento;
	}

	public List<PdtComplementoPorCid> getComplementos() {
		return complementos;
	}

	public void setComplementos(List<PdtComplementoPorCid> complementos) {
		this.complementos = complementos;
	}

	public PdtComplementoPorCid getComplemento() {
		return complemento;
	}

	public void setComplemento(PdtComplementoPorCid complemento) {
		this.complemento = complemento;
	}
	
}
