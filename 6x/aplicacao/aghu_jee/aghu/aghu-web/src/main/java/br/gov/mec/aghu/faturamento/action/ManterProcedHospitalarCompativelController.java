package br.gov.mec.aghu.faturamento.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.FatCompatExclusItem;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatProcedimentosHospitalares;
import br.gov.mec.aghu.model.FatTipoTransplante;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

public class ManterProcedHospitalarCompativelController extends ActionController {

	@PostConstruct
	protected void init() {
		begin(conversation);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -2425440662972271628L;

	private static final Log LOG = LogFactory.getLog(ManterProcedHospitalarCompativelController.class);

	private static final String REDIRECIONA_LISTAGEM_PROC_HOSP_COMPAT = "manterProcedHospitalarCompativelList";

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	@EJB
	private IParametroFacade parametroFacade;

	private boolean editando;

	private boolean alterou;

	// Insert e update
	private FatCompatExclusItem fatCompatExclusItem;

	private List<FatCompatExclusItem> lista = null;

	private List<FatCompatExclusItem> listaClonada = null;

	// Display
	private FatProcedimentosHospitalares procedimentoHospitalar;

	private FatItensProcedHospitalar itensProcedHospitalar;

	// Campos auxiliares para a suggestion
	private FatProcedimentosHospitalares procedimentoHospitalarSuggestion;

	private FatItensProcedHospitalar itemProcedHospitalarSuggestion;

	// Parametros
	private String origem;

	private Short phoSeq;

	private Integer seq;

	private int itemExcluir;

	private List<FatCompatExclusItem> listaExcluidos = null;

	public enum ManterProcedHospitalarCompativelControllerExceptionCode implements BusinessExceptionCode {
		PROCEDIMENTO_HOSPITALAR_COMPATIVEL_EXCLUSIVO_GRAVADO_SUCESSO, CAMPO_TABELA_NAO_PREENCHIDO, FAT_01104, ITEM_JA_INSERIDO_COMPARACAO;
	}

	public void inicio() {
	 

		if (itensProcedHospitalar == null || itensProcedHospitalar.getSeq() == null) {
			if (phoSeq != null && seq != null) {
				itensProcedHospitalar = faturamentoFacade.obterItemProcedHospitalar(phoSeq, seq);
				inicio(itensProcedHospitalar);
			}
		} else {
			inicio(itensProcedHospitalar);
		}
	
	}

	public void inicio(FatItensProcedHospitalar procedimentoSelecionado) {

		procedimentoHospitalar = procedimentoSelecionado.getProcedimentoHospitalar();
		itensProcedHospitalar = procedimentoSelecionado;

		listaExcluidos = new ArrayList<FatCompatExclusItem>();
		inicializaFatCompatExclusItem();
		editando = false;
		alterou = false;

		try {
			procedimentoHospitalarSuggestion = faturamentoFacade.obterProcedimentoHospitalar(parametroFacade
					.buscarAghParametro(AghuParametrosEnum.P_TABELA_FATUR_PADRAO).getVlrNumerico().shortValue());

			lista = this.faturamentoFacade.listarFatCompatExclusItem(procedimentoSelecionado.getId().getPhoSeq(), procedimentoSelecionado
					.getId().getSeq(), DominioSituacao.A);
			if (lista == null) {
				lista = new ArrayList<FatCompatExclusItem>();
			}

			for (FatCompatExclusItem aux : lista) {
				aux.setModificado(false);
			}

			listaClonada = faturamentoFacade.clonarFatCompatExclusItem(lista);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(), e);
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
			LOG.error(e.getMessage(), e);
		}

	}

	private void inicializaFatCompatExclusItem() {
		fatCompatExclusItem = new FatCompatExclusItem();
		FatItensProcedHospitalar fatItensProcedHospitalar = faturamentoFacade.obterItemProcedHospitalar(itensProcedHospitalar.getId()
				.getPhoSeq(), itensProcedHospitalar.getId().getSeq());
		fatCompatExclusItem.setItemProcedHosp(fatItensProcedHospitalar);
		fatCompatExclusItem.setQuantidadeMaxima(null);
		fatCompatExclusItem.setTipoTransplante(null);
		fatCompatExclusItem.setIndInternacao(false);
		fatCompatExclusItem.setIndAmbulatorio(false);
		itemProcedHospitalarSuggestion = null;
	}

	/**
	 * Método para pesquisar FAT_PROCEDIMENTOS_HOSPITALARES na suggestion da
	 * tela
	 * 
	 * @return
	 */
	public List<FatProcedimentosHospitalares> pesquisarFatProcedimentosHospitalares(String param) {
		return this.returnSGWithCount(faturamentoFacade.listarProcedimentosHospitalaresPorSeqEDescricaoOrdenado(param,
				FatProcedimentosHospitalares.Fields.SEQ.toString()),pesquisarFatProcedimentosHospitalaresCount(param));
	}

	public Long pesquisarFatProcedimentosHospitalaresCount(String param) {
		return faturamentoFacade.listarProcedimentosHospitalaresPorSeqEDescricaoCount(param);
	}

	public List<FatItensProcedHospitalar> pesquisarFatItensProcedHospitalarPorPhoSeqECodTabela(String param) {
		if (procedimentoHospitalarSuggestion == null) { // se o usuario nao
														// informou a tabela

			apresentarMsgNegocio(ManterProcedHospitalarCompativelControllerExceptionCode.CAMPO_TABELA_NAO_PREENCHIDO.toString());

			return this.returnSGWithCount(new ArrayList<FatItensProcedHospitalar>(),pesquisarFatItensProcedHospitalarPorPhoSeqECodTabelaCount(param));
		} else {
			return faturamentoFacade.listarFatItensProcedHospitalarPorPhoSeqECodTabela(procedimentoHospitalarSuggestion.getSeq(), param,
					100);
		}
	}

	public Long pesquisarFatItensProcedHospitalarPorPhoSeqECodTabelaCount(String param) {
		if (procedimentoHospitalarSuggestion == null) { // se o usuario nao
														// informou a tabela
			return 0L;
		} else {
			return faturamentoFacade.listarFatItensProcedHospitalarPorPhoSeqESeqCount(procedimentoHospitalarSuggestion.getSeq(), param);
		}
	}

	public String gravar() {
		try {
			faturamentoFacade.persistirFatCompatExclusItem(lista, listaClonada, listaExcluidos);
			listaClonada = faturamentoFacade.clonarFatCompatExclusItem(lista);

			apresentarMsgNegocio(ManterProcedHospitalarCompativelControllerExceptionCode.PROCEDIMENTO_HOSPITALAR_COMPATIVEL_EXCLUSIVO_GRAVADO_SUCESSO
					.toString());
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
			LOG.error(e.getMessage(), e);
		}

		if (origem == null || origem.equals("")) {
			return REDIRECIONA_LISTAGEM_PROC_HOSP_COMPAT;
		}

		return origem;
	}

	public String cancelar() {
		if (origem == null || origem.equals("")) {
			return REDIRECIONA_LISTAGEM_PROC_HOSP_COMPAT;
		} else {
			return origem;
		}
	}

	/**
	 * Retorna os tipos de transplante para o dropDown
	 * 
	 * @return
	 */
	public List<FatTipoTransplante> getTipoTransplanteItens() {
		return faturamentoFacade.listarTodosOsTiposTransplante();
	}

	public String limpaSuggestion() {
		itemProcedHospitalarSuggestion = null;
		return null;
	}

	public String adicionar(boolean confirmacao) {
		try {
			if (!fatCompatExclusItem.getIndAmbulatorio() && !fatCompatExclusItem.getIndInternacao()) {
				throw new ApplicationBusinessException(ManterProcedHospitalarCompativelControllerExceptionCode.FAT_01104);
			}

			if (estaLista() && !confirmacao) {
				throw new ApplicationBusinessException(ManterProcedHospitalarCompativelControllerExceptionCode.ITEM_JA_INSERIDO_COMPARACAO);
			}

			alterou = true;

			fatCompatExclusItem.setItemProcedHospCompatibiliza(itemProcedHospitalarSuggestion);
			fatCompatExclusItem.getItemProcedHospCompatibiliza().setProcedimentoHospitalar(procedimentoHospitalarSuggestion);
			fatCompatExclusItem.setModificado(true);
			lista.add(fatCompatExclusItem);
			gravar();

			inicializaFatCompatExclusItem();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}

		return null;
	}

	/**
	 * Verifica se o tipo e a comparacao já estão na lista
	 * 
	 * @return
	 */
	private boolean estaLista() {
		for (FatCompatExclusItem aux : lista) {
			if (aux.getIndCompatExclus().equals(fatCompatExclusItem.getIndCompatExclus())
					&& aux.getIndComparacao().equals(fatCompatExclusItem.getIndComparacao())
					&& aux.getItemProcedHospCompatibiliza().getId().getPhoSeq().equals(itemProcedHospitalarSuggestion.getId().getPhoSeq())
					&& aux.getItemProcedHospCompatibiliza().getId().getSeq().equals(itemProcedHospitalarSuggestion.getId().getSeq())) {
				return true;
			}
		}
		return false;
	}

	public void editarProcedimento(FatCompatExclusItem fatCompatExclusItem) {
		editando = true;
		this.fatCompatExclusItem = fatCompatExclusItem;
		itemProcedHospitalarSuggestion = fatCompatExclusItem.getItemProcedHospCompatibiliza();
		procedimentoHospitalarSuggestion = fatCompatExclusItem.getItemProcedHospCompatibiliza().getProcedimentoHospitalar();
	}

	public String confirmarEdicao() {
		try {
			if (!fatCompatExclusItem.getIndAmbulatorio() && !fatCompatExclusItem.getIndInternacao()) {
				throw new ApplicationBusinessException(ManterProcedHospitalarCompativelControllerExceptionCode.FAT_01104);
			}
			editando = false;
			alterou = true;
			lista.remove(fatCompatExclusItem);
			adicionar(true);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		return null;
	}

	public String cancelarEdicao() {
		editando = false;
		inicializaFatCompatExclusItem();
		return null;
	}

	public void remover() {
		alterou = true;
		listaExcluidos.add(lista.remove(itemExcluir));
		gravar();
	}

	/************************ GETTERS AND SETTERS *************************/
	public FatProcedimentosHospitalares getProcedimentoHospitalar() {
		return procedimentoHospitalar;
	}

	public void setProcedimentoHospitalar(FatProcedimentosHospitalares procedimentoHospitalar) {
		this.procedimentoHospitalar = procedimentoHospitalar;
	}

	public FatItensProcedHospitalar getItensProcedHospitalar() {
		return itensProcedHospitalar;
	}

	public void setItensProcedHospitalar(FatItensProcedHospitalar itensProcedHospitalar) {
		this.itensProcedHospitalar = itensProcedHospitalar;
	}

	public FatCompatExclusItem getFatCompatExclusItem() {
		return fatCompatExclusItem;
	}

	public void setFatCompatExclusItem(FatCompatExclusItem fatCompatExclusItem) {
		this.fatCompatExclusItem = fatCompatExclusItem;
	}

	public FatProcedimentosHospitalares getProcedimentoHospitalarSuggestion() {
		return procedimentoHospitalarSuggestion;
	}

	public void setProcedimentoHospitalarSuggestion(FatProcedimentosHospitalares procedimentoHospitalarSuggestion) {
		this.procedimentoHospitalarSuggestion = procedimentoHospitalarSuggestion;
	}

	public FatItensProcedHospitalar getItemProcedHospitalarSuggestion() {
		return itemProcedHospitalarSuggestion;
	}

	public void setItemProcedHospitalarSuggestion(FatItensProcedHospitalar itemProcedHospitalarSuggestion) {
		this.itemProcedHospitalarSuggestion = itemProcedHospitalarSuggestion;
	}

	public List<FatCompatExclusItem> getLista() {
		return lista;
	}

	public void setLista(List<FatCompatExclusItem> lista) {
		this.lista = lista;
	}

	public boolean isEditando() {
		return editando;
	}

	public void setEditando(boolean editando) {
		this.editando = editando;
	}

	public Integer getItemExcluir() {
		return itemExcluir;
	}

	public void setItemExcluir(Integer itemExcluir) {
		this.itemExcluir = itemExcluir;
	}

	public List<FatCompatExclusItem> getListaExcluidos() {
		return listaExcluidos;
	}

	public void setListaExcluidos(List<FatCompatExclusItem> listaExcluidos) {
		this.listaExcluidos = listaExcluidos;
	}

	public boolean isAlterou() {
		return alterou;
	}

	public void setAlterou(boolean alterou) {
		this.alterou = alterou;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
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

}
