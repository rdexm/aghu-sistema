package br.gov.mec.aghu.faturamento.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.FatCaractComplexidade;
import br.gov.mec.aghu.model.FatCaractFinanciamento;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatProcedimentosHospitalares;
import br.gov.mec.aghu.model.FatTipoTransplante;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;



public class ManterComplexidadeFinanciamentoController extends ActionController {

	private static final String MANTER_ITEM_PRINCIPAL = "manterItemPrincipal";

	

	private static final Log LOG = LogFactory.getLog(ManterComplexidadeFinanciamentoController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -2425440662972271628L;

	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	//Display
	private FatProcedimentosHospitalares procedimentoHospitalar;
	private FatItensProcedHospitalar itensProcedHospitalar;
	private FatItensProcedHospitalar itensProcedHospitalarClone;
	
	//Campos auxiliares para a suggestion
	private FatCaractFinanciamento fatCaractFinanciamentoSuggestion;
	private FatCaractComplexidade fatCaractComplexidadeSuggestion; 
	
	//Parametros
	private Short tabela;
	private Short phoSeq;
	private Integer seq;
	private String origem;
	
	public enum ManterComplexidadeFinanciamentoControllerExceptionCode implements
	BusinessExceptionCode {
		PROCEDIMENTO_HOSPITALAR_COMPATIVEL_EXCLUSIVO_GRAVADO_SUCESSO,
		CAMPO_TABELA_NAO_PREENCHIDO,
		FAT_01104,
		ITEM_JA_INSERIDO_COMPARACAO;
	}

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void inicio() {
	 
		

		if (seq != null) {
			
			try {
				this.procedimentoHospitalar = this.faturamentoFacade.obterProcedimentoHospitalar(phoSeq);
				this.itensProcedHospitalar = this.faturamentoFacade.obterItemProcedHospitalar(phoSeq, seq);
			
				itensProcedHospitalarClone = this.faturamentoFacade.clonarItemProcedimentoHospitalar(itensProcedHospitalar);
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
				LOG.error(e.getMessage(), e);
			}
			catch (Exception e) {
				this.apresentarMsgNegocio(Severity.ERROR, e.getMessage());
				LOG.error(e.getMessage(), e);
			}
			
		} else {
			this.procedimentoHospitalar = null;
			this.itensProcedHospitalar = null;
		}
		
		if (itensProcedHospitalar != null) {
			this.setFatCaractFinanciamentoSuggestion(this.faturamentoFacade.obterCaractFinanciamentoPorSeqEPhoSeqECodTabela(phoSeq, seq, itensProcedHospitalar.getCodTabela()));
			this.setFatCaractComplexidadeSuggestion(this.faturamentoFacade.obterCaractComplexidadePorSeqEPhoSeqECodTabela(phoSeq, seq, itensProcedHospitalar.getCodTabela()));
		} else {
			this.setFatCaractFinanciamentoSuggestion(null); 
			this.setFatCaractComplexidadeSuggestion(null); 
		}
	
	}
	
	/**
	 * Método para pesquisar FAT_PROCEDIMENTOS_HOSPITALARES na suggestion da tela
	 * 
	 * @return
	 */
	public List<FatProcedimentosHospitalares> pesquisarFatProcedimentosHospitalares(Object param) {
		return faturamentoFacade.listarProcedimentosHospitalaresPorSeqEDescricaoOrdenado(param, FatProcedimentosHospitalares.Fields.SEQ.toString());
	}
	
	public Long pesquisarFatProcedimentosHospitalaresCount(Object param) {
		return faturamentoFacade.listarProcedimentosHospitalaresPorSeqEDescricaoCount(param);
	}

	public void gravar() {
		try {
			RapServidores servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(), new Date());
			itensProcedHospitalar.setFatCaracteristicaFinanciamento(this.getFatCaractFinanciamentoSuggestion());
			itensProcedHospitalar.setCaracteristicaComplexidade(this.getFatCaractComplexidadeSuggestion());
			faturamentoFacade.atualizarItemProcedimentoHospitalar(itensProcedHospitalar, itensProcedHospitalarClone, servidorLogado);

			this.apresentarMsgNegocio(Severity.INFO,
					ManterComplexidadeFinanciamentoControllerExceptionCode.PROCEDIMENTO_HOSPITALAR_COMPATIVEL_EXCLUSIVO_GRAVADO_SUCESSO.toString());
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		catch (Exception e) {
			this.apresentarMsgNegocio(Severity.ERROR, e.getLocalizedMessage());
			LOG.error(e.getMessage(), e);
		}
	}
	
	public String cancelar() {
		return MANTER_ITEM_PRINCIPAL;
	}			
	
	/** Retorna os tipos de transplante para o dropDown
	 * 
	 * @return
	 */
	public List<FatTipoTransplante> getTipoTransplanteItens() {
		return faturamentoFacade.listarTodosOsTiposTransplante();
	}
	
	public List<FatCaractComplexidade> listarComplexidades(String objPesquisa){
		return this.faturamentoFacade.listarComplexidadesAtivasPorCodigoOuDescricao(objPesquisa);
	}
	
	public List<FatCaractFinanciamento> listarFinanciamentos(String objPesquisa){
		return this.faturamentoFacade.listarFinanciamentosAtivosPorCodigoOuDescricao(objPesquisa);
	}

	public Long listarFinanciamentosCount(Object objPesquisa){
		return this.faturamentoFacade.listarCbosCount(objPesquisa);
	}
	
	public Long listarComplexidadesCount(Object objPesquisa){
		return this.faturamentoFacade.listarCbosCount(objPesquisa);
	}	
	
	/************************ GETTERS AND SETTERS *************************/
	public FatProcedimentosHospitalares getProcedimentoHospitalar() {
		return procedimentoHospitalar;
	}

	public void setProcedimentoHospitalar(
			FatProcedimentosHospitalares procedimentoHospitalar) {
		this.procedimentoHospitalar = procedimentoHospitalar;
	}

	public FatItensProcedHospitalar getItensProcedHospitalar() {
		return itensProcedHospitalar;
	}

	public void setItensProcedHospitalar(
			FatItensProcedHospitalar itensProcedHospitalar) {
		this.itensProcedHospitalar = itensProcedHospitalar;
	}

	public Short getTabela() {
		return tabela;
	}

	public void setTabela(Short tabela) {
		this.tabela = tabela;
	}

	public Short getPhoSeq() {
		return phoSeq;
	}

	public void setPhoSeq(Short phoSeq) {
		this.phoSeq = phoSeq;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public FatItensProcedHospitalar getItensProcedHospitalarClone() {
		return itensProcedHospitalarClone;
	}

	public void setItensProcedHospitalarClone(FatItensProcedHospitalar itensProcedHospitalarClone) {
		this.itensProcedHospitalarClone = itensProcedHospitalarClone;
	}

	public FatCaractFinanciamento getFatCaractFinanciamentoSuggestion() {
		return fatCaractFinanciamentoSuggestion;
	}

	public void setFatCaractFinanciamentoSuggestion(
			FatCaractFinanciamento fatCaractFinanciamentoSuggestion) {
		this.fatCaractFinanciamentoSuggestion = fatCaractFinanciamentoSuggestion;
	}

	public FatCaractComplexidade getFatCaractComplexidadeSuggestion() {
		return fatCaractComplexidadeSuggestion;
	}

	public void setFatCaractComplexidadeSuggestion(
			FatCaractComplexidade fatCaractComplexidadeSuggestion) {
		this.fatCaractComplexidadeSuggestion = fatCaractComplexidadeSuggestion;
	}

}