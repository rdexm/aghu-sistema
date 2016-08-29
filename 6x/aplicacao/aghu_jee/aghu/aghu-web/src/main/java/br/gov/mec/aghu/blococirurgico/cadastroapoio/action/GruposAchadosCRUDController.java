package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.blococirurgico.procdiagterap.business.IBlocoCirurgicoProcDiagTerapFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.PdtAchado;
import br.gov.mec.aghu.model.PdtGrupo;
import br.gov.mec.aghu.model.PdtGrupoId;
import br.gov.mec.aghu.model.PdtProcDiagTerap;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class GruposAchadosCRUDController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final long serialVersionUID = -9080940970337693983L;

	@EJB
	private IBlocoCirurgicoProcDiagTerapFacade blocoCirurgicoProcDiagTerapFacade;
	
	private static final String GRUPO_LIST = "gruposAchadosList";
	
	private Integer dptSeq;
	private Short seqp;
	
	private Boolean ativoGrupo;
	private Boolean ativoAchado; 	
		
	private PdtProcDiagTerap procDiagTerap; 	
	
	private PdtGrupo grupo;
	private PdtAchado achado;
	
		
	private List<PdtAchado> listaAchados; 
	
	private Boolean ativaBotaoGravarAchado; 
	private Boolean ativaCrudAchado; 

	
	public void inicio() {
	 

	 
		
		grupo = new PdtGrupo();
		achado = new PdtAchado();
		if (dptSeq != null && seqp != null) {				
			grupo.setId(new PdtGrupoId(dptSeq,seqp));
			grupo = blocoCirurgicoProcDiagTerapFacade.obterPdtGrupoPorId(grupo.getId());
			if (DominioSituacao.A.equals(grupo.getIndSituacao())) { 			
				ativoGrupo = Boolean.TRUE;
			} else {
				ativoGrupo = Boolean.FALSE;
			}
 			procDiagTerap = blocoCirurgicoProcDiagTerapFacade.obterPdtProcDiagTerap(dptSeq);
 			carregarListaAchadosPorGrupo(); 					
 			ativaCrudAchado = Boolean.TRUE;		
 			ativoAchado = Boolean.TRUE;	
 			ativaBotaoGravarAchado = Boolean.TRUE; 	 			
		} else {	
			ativoGrupo = Boolean.TRUE;			
			ativaCrudAchado = Boolean.FALSE;
		}					
	
	}
	
	
	public List<PdtProcDiagTerap> pesquisarPdtProcDiagTerap(String strPesquisa) {
		return this.returnSGWithCount(this.blocoCirurgicoProcDiagTerapFacade.pesquisarPdtProcDiagTerap((String) strPesquisa),pesquisarPdtProcDiagTerapCount(strPesquisa));
	}

	public Long pesquisarPdtProcDiagTerapCount(String strPesquisa) {
		return this.blocoCirurgicoProcDiagTerapFacade.pesquisarPdtProcDiagTerapCount((String) strPesquisa);
	}
	
	public void gravarGrupo() {
		try {
			if (ativoGrupo) {
				grupo.setIndSituacao(DominioSituacao.A);
			} else {
				grupo.setIndSituacao(DominioSituacao.I);
			}
			String mensagemSucesso = blocoCirurgicoProcDiagTerapFacade.gravarPdtGrupo(grupo, procDiagTerap.getSeq());
			apresentarMsgNegocio(Severity.INFO, mensagemSucesso);		
			ativaCrudAchado = Boolean.TRUE;				
			ativoAchado = Boolean.TRUE;	
			ativaBotaoGravarAchado = Boolean.TRUE; 		
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);				
		}		
	}
	
	public String cancelar() {		
		setDptSeq(null);
		setSeqp(null);
		setAchado(null);
		setGrupo(null);
		return GRUPO_LIST;
	}
			
	public void carregarListaAchadosPorGrupo() {
		listaAchados = blocoCirurgicoProcDiagTerapFacade.pesquisarPdtAchados(grupo.getId().getDptSeq(), grupo.getId().getSeqp());
		blocoCirurgicoProcDiagTerapFacade.refreshPdtAchado(listaAchados);
	}
		
	public void cancelarEdicaoAchado() {
		ativoAchado = Boolean.TRUE;	
		ativaBotaoGravarAchado = Boolean.TRUE;	
		blocoCirurgicoProcDiagTerapFacade.refreshPdtAchado(listaAchados);
		achado = new PdtAchado();				
	}
	
	public void selecionarAchado(PdtAchado achado) {		
		this.achado = achado;
		if (DominioSituacao.A.equals(achado.getIndSituacao())) { 			
			ativoAchado = Boolean.TRUE;
		} else {
			ativoAchado = Boolean.FALSE;
		}		
		ativaBotaoGravarAchado = Boolean.FALSE;			
	}	
	
	public void gravarAchado() {		
		try {
			if (ativoAchado) {
				achado.setIndSituacao(DominioSituacao.A);
			} else {
				achado.setIndSituacao(DominioSituacao.I);
			}
			String mensagemSucesso = blocoCirurgicoProcDiagTerapFacade.gravarPdtAchado(grupo, achado);
			apresentarMsgNegocio(Severity.INFO, mensagemSucesso);
			achado = new PdtAchado();			
			carregarListaAchadosPorGrupo();	
			ativoAchado = Boolean.TRUE;	
			ativaBotaoGravarAchado = Boolean.TRUE;				
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);	
		}
	}	
	
	public String editarBoolean(Boolean boleano) {
		String retorno = null;
		if (boleano != null){
			retorno = boleano ? "Sim" : "Não"; 
		}
		return retorno;
	}	
	
	
	// Getters e Setters

	public Boolean getAtivoGrupo() {
		return ativoGrupo;
	}

	public void setAtivoGrupo(Boolean ativoGrupo) {
		this.ativoGrupo = ativoGrupo;
	}

	public PdtGrupo getGrupo() {
		return grupo;
	}

	public void setGrupo(PdtGrupo grupo) {
		this.grupo = grupo;
	}

	public PdtAchado getAchado() {
		return achado;
	}

	public void setAchado(PdtAchado achado) {
		this.achado = achado;
	}

	public Boolean getAtivaBotaoGravarAchado() {
		return ativaBotaoGravarAchado;
	}

	public void setAtivaBotaoGravarAchado(Boolean ativaBotaoGravarAchado) {
		this.ativaBotaoGravarAchado = ativaBotaoGravarAchado;
	}

	public Boolean getAtivaCrudAchado() {
		return ativaCrudAchado;
	}

	public void setAtivaCrudAchado(Boolean ativaCrudAchado) {
		this.ativaCrudAchado = ativaCrudAchado;
	}

	public PdtProcDiagTerap getProcDiagTerap() {
		return procDiagTerap;
	}

	public void setProcDiagTerap(PdtProcDiagTerap procDiagTerap) {
		this.procDiagTerap = procDiagTerap;
	}

	public Boolean getAtivoAchado() {
		return ativoAchado;
	}

	public void setAtivoAchado(Boolean ativoAchado) {
		this.ativoAchado = ativoAchado;
	}

	public List<PdtAchado> getListaAchados() {
		return listaAchados;
	}

	public void setListaAchados(List<PdtAchado> listaAchados) {
		this.listaAchados = listaAchados;
	}

	public Integer getDptSeq() {
		return dptSeq;
	}

	public void setDptSeq(Integer dptSeq) {
		this.dptSeq = dptSeq;
	}

	public Short getSeqp() {
		return seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}	

}