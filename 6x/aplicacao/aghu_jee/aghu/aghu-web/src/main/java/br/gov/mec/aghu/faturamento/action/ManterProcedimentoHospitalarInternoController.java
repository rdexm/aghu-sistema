package br.gov.mec.aghu.faturamento.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.MamItemExame;
import br.gov.mec.aghu.model.MamItemMedicacao;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;


public class ManterProcedimentoHospitalarInternoController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3435153895207639924L;

	private final String REDIRECIONA_LISTAR_PHI = "manterProcedimentoHospitalarInternoList";
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;

	@Inject
	private ManterProcedimentoHospitalarInternoPaginatorController manterProcedimentoHospitalarInternoPaginatorController;

	//Insert e update
	private FatProcedHospInternos procedHospInterno;
	private Boolean somenteAtivo;	
	private Boolean camposReadOnly;
	private Boolean incapacitarDescricaoPhi = Boolean.FALSE;
	private List<MamItemMedicacao> itensMedicacao;

	public enum ManterProcedimentoHospitalarInternoControllerExceptionCode implements
	BusinessExceptionCode {
		LABEL_PROCEDIMENTO_HOSPITALAR_INTERNO_GRAVADO_SUCESSO,
		LABEL_NUMERO_INVALIDO;
	}

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}
	
	public void inicio() {		
		itensMedicacao = listarTodosItensMedicacao();
		
		if (procedHospInterno != null) {
			this.incapacitarDescricaoPhi = isIncapacitarDescricaoPHI();
			procedHospInterno.setDescricao(procedHospInterno.getDescricao().trim());
			if (procedHospInterno.getSituacao().isAtivo()) {
				somenteAtivo = true;
			} else {
				somenteAtivo = false;
			}
			
			//Os campos de fator conversão e operação conversão só podem ser editados caso o seguinte SQL retorne resultado
			camposReadOnly = true;
			if (procedHospInterno.getMaterial() != null) {
				List<FatProcedHospInternos> lista = faturamentoFacade.listarFatProcedHospInternosPorMaterial(procedHospInterno.getMaterial());
				camposReadOnly = lista.isEmpty();
			}
		}
		else {
			this.procedHospInterno = new FatProcedHospInternos();
			somenteAtivo = true;
			camposReadOnly = false;
		}
	}
	
	public String gravar() {
		if (somenteAtivo) {
			procedHospInterno.setSituacao(DominioSituacao.A);
		} else {
			procedHospInterno.setSituacao(DominioSituacao.I);
		}
		
		try {
			faturamentoFacade.persistirProcedimentoHospitalarInterno(procedHospInterno);
			
			manterProcedimentoHospitalarInternoPaginatorController.pesquisar();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		procedHospInterno = null;
		return REDIRECIONA_LISTAR_PHI;		
	}

	public String cancelar() {
		procedHospInterno = null;
		return REDIRECIONA_LISTAR_PHI;		
	}
    private Boolean isIncapacitarDescricaoPHI(){
			try {
				return !(faturamentoFacade.buscarParametroEditarDescPHI().getVlrTexto().equalsIgnoreCase(String.valueOf(DominioSimNao.S.getDescricao().charAt(0))));
			} catch (BaseException e) {
				return Boolean.FALSE;
			}
    }
	/** Pesquisa para Suggestion procedimentos cirurgicos
	 * 
	 * @param param
	 * @return
	 */
	public List<MbcProcedimentoCirurgicos> pesquisarProcedimentosCirurgicos(String param) {
		return this.returnSGWithCount(blocoCirurgicoFacade.pesquisarProcedimentosCirurgicos(param, MbcProcedimentoCirurgicos.Fields.SEQ.toString(), 100, DominioSituacao.A),pesquisarProcedimentosCirurgicosCount(param));
	}
	
	public Long pesquisarProcedimentosCirurgicosCount(String param) {
		return blocoCirurgicoFacade.pesquisarProcedimentosCirurgicosCount(param, DominioSituacao.A);
	}	

	/**
	 * Pesquisa para Suggestion procedimentos hospitalares agrupados
	 * 
	 * @param param
	 * @return
	 */
	public List<FatProcedHospInternos> pesquisarProcedimentosHospitalaresAgrupados(String param) {
		return this.returnSGWithCount(faturamentoFacade.listarProcedimentosHospitalaresAgrupadosPorPhiSeqOuDescricao(param, FatProcedHospInternos.Fields.DESCRICAO.toString(), DominioSituacao.A),pesquisarProcedimentosHospitalaresAgrupadosCount(param));
	}
	
	public Long pesquisarProcedimentosHospitalaresAgrupadosCount(String param) {
		return faturamentoFacade.listarProcedimentosHospitalaresAgrupadosPorPhiSeqOuDescricaoCount(param, DominioSituacao.A);
	}	
	
	public List<MamItemExame> pesquisarItemExame(String param) {
		return this.returnSGWithCount(ambulatorioFacade.pesquisarItemExamePorDescricaoOuSeq(param, MamItemExame.Fields.SEQ.toString(), 100),pesquisarItemExameCount(param));		
	}
	
	public Long pesquisarItemExameCount(String param) {
		return ambulatorioFacade.pesquisarItemExamePorDescricaoOuSeq(param);		
	}
	
	private List<MamItemMedicacao> listarTodosItensMedicacao() {
		return ambulatorioFacade.listarTodosItensMedicacao(MamItemMedicacao.Fields.SEQ.toString());
	}

	public FatProcedHospInternos getProcedHospInterno() {
		return procedHospInterno;
	}

	public void setProcedHospInterno(FatProcedHospInternos procedHospInterno) {
		this.procedHospInterno = procedHospInterno;
	}

	public Boolean getSomenteAtivo() {
		return somenteAtivo;
	}

	public void setSomenteAtivo(Boolean somenteAtivo) {
		this.somenteAtivo = somenteAtivo;
	}

	public Boolean getCamposReadOnly() {
		return camposReadOnly;
	}

	public void setCamposReadOnly(Boolean camposReadOnly) {
		this.camposReadOnly = camposReadOnly;
	}

	public List<MamItemMedicacao> getItensMedicacao() {
		return itensMedicacao;
	}

	public void setItensMedicacao(List<MamItemMedicacao> itensMedicacao) {
		this.itensMedicacao = itensMedicacao;
	}

	public Boolean getIncapacitarDescricaoPhi() {
		return incapacitarDescricaoPhi;
	}

	public void setIncapacitarDescricaoPhi(Boolean incapacitarDescricaoPhi) {
		this.incapacitarDescricaoPhi = incapacitarDescricaoPhi;
	}
	
}
