package br.gov.mec.aghu.faturamento.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioCobrancaDiaria;
import br.gov.mec.aghu.dominio.DominioFideps;
import br.gov.mec.aghu.dominio.DominioModoLancamentoFat;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioUtilizacaoItem;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.FatCaractItemProcHosp;
import br.gov.mec.aghu.model.FatFormaOrganizacao;
import br.gov.mec.aghu.model.FatGrupo;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatItensProcedHospitalarId;
import br.gov.mec.aghu.model.FatMotivoCobrancaApac;
import br.gov.mec.aghu.model.FatProcedimentosHospitalares;
import br.gov.mec.aghu.model.FatSubGrupo;
import br.gov.mec.aghu.model.FatTipoAto;
import br.gov.mec.aghu.model.FatTipoCaractItens;
import br.gov.mec.aghu.model.FatTiposVinculo;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.WebUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterItemPrincipalController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(ManterItemPrincipalController.class);

    private static final String MANTER_ITEM_PRINCIPAL = "faturamento-manterItemPrincipal";

	/**
	 * 
	 */
	private static final long serialVersionUID = 4133707316875526323L;

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	private FatItensProcedHospitalar itemProcedHosp;

	private FatItensProcedHospitalar itemProcedHospClone;

	private AghClinicas clinica;

	private FatProcedimentosHospitalares procedimentoHospitalar;

	private FatTipoAto tipoAto;

	private FatTiposVinculo tipoVinculo;

	private FatMotivoCobrancaApac motivoCobrancaAPAC;

	private List<FatCaractItemProcHosp> listaCaractr;

	private Integer seq;

	private Short phoSeq;

	private Boolean situacao;

	private FatGrupo grupo;

	private FatSubGrupo subGrupo;

	private FatFormaOrganizacao formaOrganizacao;

	public enum ManterItemPrincipalControllerExceptionCode implements BusinessExceptionCode {
		ITEM_CONTA_HOSPITALAR_INCLUIDA_SUCESSO, ITEM_CONTA_HOSPITALAR_ALTERADA_SUCESSO, IDADE_MINIMA_DEVE_SER_MAIOR_IGUAL_ZERO, IDADE_MAXIMA_DEVE_SER_MAIOR_IGUAL_ZERO, IDADE_MAXIMA_DEVE_SER_MAIOR_IDADE_MINIMA, PERCENTUAL_SERV_PROF_NAO_DEVE_SER_MAIOR_QUE_CEM, ERRO_GENERICO_ITEM_CONTA_HOSPITALAR;
	}

	public void inicio() {
	 

		try {
			if (phoSeq != null && seq != null) {
				itemProcedHosp = faturamentoFacade.obterItemProcedHospitalarPorChavePrimaria(new FatItensProcedHospitalarId(phoSeq, seq));
				itemProcedHospClone = faturamentoFacade.clonarItemProcedimentoHospitalar(itemProcedHosp);
				procedimentoHospitalar = itemProcedHosp.getProcedimentoHospitalar();
				clinica = itemProcedHosp.getClinica();
				tipoAto = itemProcedHosp.getTipoAto();
				tipoVinculo = itemProcedHosp.getTiposVinculo();
				motivoCobrancaAPAC = itemProcedHosp.getMotivoCobrancaApac();
				listaCaractr = new ArrayList<FatCaractItemProcHosp>(0);

				if (itemProcedHosp.getSituacao() != null) {
					situacao = itemProcedHosp.getSituacao().isAtivo();
				}

				if (itemProcedHosp != null) {
					listaCaractr = faturamentoFacade.listarCaractItemProcHospPorPhoSeqECodTabela(itemProcedHosp.getId().getPhoSeq(), itemProcedHosp.getCodTabela());
				}

				// ITENS TABELA
				inicializaItensTabela();

				if (itemProcedHosp.getFormaOrganizacao() != null) {
					formaOrganizacao = itemProcedHosp.getFormaOrganizacao();
					subGrupo = itemProcedHosp.getSubGrupo();
					grupo = itemProcedHosp.getGrupo();
				}
			} else {
				itemProcedHosp = new FatItensProcedHospitalar();
				itemProcedHosp.setId(new FatItensProcedHospitalarId());
				listaCaractr = new ArrayList<FatCaractItemProcHosp>(0);
				clinica = null;
				tipoAto = null;
				tipoVinculo = null;
				motivoCobrancaAPAC = null;
				formaOrganizacao = null;
				subGrupo = null;
				grupo = null;
				situacao = true;

				// VALORES DEFAULT
				inizializaValoresDefault();

				procedimentoHospitalar = faturamentoFacade.obterFatProcedimentosHospitalaresPadrao();
			}
		} catch (Exception e) {
			LOG.error("Exceção capturada: ", e);
			apresentarMsgNegocio(Severity.INFO, ManterItemPrincipalControllerExceptionCode.ERRO_GENERICO_ITEM_CONTA_HOSPITALAR.toString());
		}
	
	}

	private void inizializaValoresDefault() {
		itemProcedHosp.setInternacao(true);
		itemProcedHosp.setIdadeMin(0);
		itemProcedHosp.setModoLancamentoFat(DominioModoLancamentoFat.O);
		itemProcedHosp.setFideps(DominioFideps.N);
		itemProcedHosp.setCobrancaDiarias(DominioCobrancaDiaria.N);
		itemProcedHosp.setProcedimentoEspecial(false);
		itemProcedHosp.setHcpaCadastrado(true);
		itemProcedHosp.setConsulta(false);
		itemProcedHosp.setExigeConsulta(true);
		itemProcedHosp.setCobrancaApac(false);
		itemProcedHosp.setCobrancaAmbulatorio(true);
		itemProcedHosp.setProcPrincipalApac(false);
		itemProcedHosp.setPsiquiatria(false);
		itemProcedHosp.setCidadeObrigatoria(false);
		itemProcedHosp.setCobrancaCma(true);
		itemProcedHosp.setFaec(false);
		itemProcedHosp.setDcihTransplante(false);
		itemProcedHosp.setBuscaDoador(false);
		itemProcedHosp.setCobraExcedenteBpa(false);
		itemProcedHosp.setTipoAih5(false);
		itemProcedHosp.setQuantidadeMaiorInternacao(true);
		itemProcedHosp.setExigeValor(true);
		itemProcedHosp.setHospDia(false);
		itemProcedHosp.setAidsPolitraumatizado(false);
		itemProcedHosp.setCobrancaConta(true);
		itemProcedHosp.setDadosParto(false);
		itemProcedHosp.setRealDifereSolic(true);
		itemProcedHosp.setSolicDifereReal(true);
		itemProcedHosp.setCobraProcedimentoEspecial(true);
		itemProcedHosp.setExigeAutorizacaoPrevia(true);
		itemProcedHosp.setPreencheCma(true);
		itemProcedHosp.setCirurgiaMultipla(false);
		itemProcedHosp.setDiariaAcompanhante(true);
	}

//	private void inicializaItensTabela() {
//		char c1 = 'D';
//		char c2 = 'E';
//		char c3 = 'L';
//		char c4 = 'E';
//		char c5 = 'T';
//		char c6 = 'E';
//
//		StringBuffer delete = new StringBuffer(6);
//		delete.append(c1);
//		delete.append(c2);
//		delete.append(c3);
//		delete.append(c4);
//		delete.append(c5);
//		delete.append(c6);
//		/* Deleta esse e descomenta o outro \/ */
//	}

	
	@SuppressWarnings({ "PMD.NPathComplexity" })
	private void inicializaItensTabela() {
		if (itemProcedHosp.getProcedimentoEspecial()) {
			listaCaractr.add(criaCaracteristica("LABEL_PROCEDIMENTO_ESPECIAL"));
		}
		if (itemProcedHosp.getHcpaCadastrado()) {
			listaCaractr.add(criaCaracteristica("LABEL_PROCEDIMENTO_CADASTRADO"));
		}
		if (itemProcedHosp.getConsulta()) {
			listaCaractr.add(criaCaracteristica("LABEL_CONSULTA"));
		}
		if (itemProcedHosp.getExigeConsulta()) {
			listaCaractr.add(criaCaracteristica("LABEL_EXIGE_CONSULTA"));
		}
		if (itemProcedHosp.getPsiquiatria()) {
			listaCaractr.add(criaCaracteristica("LABEL_PSIQUIATRIA"));
		}
		if (itemProcedHosp.getCidadeObrigatoria()) {
			listaCaractr.add(criaCaracteristica("LABEL_EXIGE_CID_SECUNDARIO"));
		}
		if (itemProcedHosp.getFaec()) {
			listaCaractr.add(criaCaracteristica("LABEL_ALTA_COMPLEXIDADE"));
		}
		if (itemProcedHosp.getDcihTransplante()) {
			listaCaractr.add(criaCaracteristica("LABEL_ESTRATEGICO"));
		}
		if (itemProcedHosp.getBuscaDoador()) {
			listaCaractr.add(criaCaracteristica("LABEL_BUSCA_DOADOR"));
		}
		if (itemProcedHosp.getCobraExcedenteBpa()) {
			listaCaractr.add(criaCaracteristica("LABEL_COBRA_EXCEDENTE_APAC_BPA"));
		}
		if (itemProcedHosp.getTipoAih5()) {
			listaCaractr.add(criaCaracteristica("LABEL_AIH5"));
		}
		if (itemProcedHosp.getQuantidadeMaiorInternacao()) {
			listaCaractr.add(criaCaracteristica("LABEL_QTD_MAIOR_INTERNACAO"));
		}
		if (itemProcedHosp.getExigeValor()) {
			listaCaractr.add(criaCaracteristica("LABEL_EXIGE_VALOR"));
		}
		if (itemProcedHosp.getHospDia()) {
			listaCaractr.add(criaCaracteristica("LABEL_HOSPITAL_DIA"));
		}
		if (itemProcedHosp.getAidsPolitraumatizado()) {
			listaCaractr.add(criaCaracteristica("LABEL_AIDS"));
		}
		if (itemProcedHosp.getCobrancaConta()) {
			listaCaractr.add(criaCaracteristica("LABEL_COBRANCA_CONTA"));
		}
		if (itemProcedHosp.getDadosParto()) {
			listaCaractr.add(criaCaracteristica("LABEL_EXIGE_DADOS_PARTO"));
		}
		if (itemProcedHosp.getRealDifereSolic()) {
			listaCaractr.add(criaCaracteristica("LABEL_REALIZADO_DIFERENTE_SOLICITADO"));
		}
		if (itemProcedHosp.getSolicDifereReal()) {
			listaCaractr.add(criaCaracteristica("LABEL_SOLICITADO_DIFERENTE_REALIZADO"));
		}
		if (itemProcedHosp.getCobraProcedimentoEspecial()) {
			listaCaractr.add(criaCaracteristica("LABEL_COBRANCA_PROCEDIMENTO_ESPECIAL"));
		}
		if (itemProcedHosp.getCirurgiaMultipla()) {
			listaCaractr.add(criaCaracteristica("LABEL_CIRURGIA_MULTIPLA_POLITRAUMATIZADO"));
		}
		if (itemProcedHosp.getDiariaAcompanhante()) {
			listaCaractr.add(criaCaracteristica("LABEL_DIARIA_ACOMPANHANTE"));
		}

		if (listaCaractr != null && !listaCaractr.isEmpty()) {
			Collections.sort(this.listaCaractr, new BeanComparator(FatCaractItemProcHosp.Fields.TIPO_CARACTERISTICA_ITEM.toString() + "." + FatTipoCaractItens.Fields.CARACTERISTICA.toString()));
		}
	}

	private FatCaractItemProcHosp criaCaracteristica(final String nomeCaracteristica) {
		FatCaractItemProcHosp caract = new FatCaractItemProcHosp();
		FatTipoCaractItens tipoCar = new FatTipoCaractItens();
		tipoCar.setCaracteristica(getMensagem(nomeCaracteristica));
		caract.setValorChar("S");
		caract.setTipoCaracteristicaItem(tipoCar);
		return caract;
	}
	 

	protected String getMensagem(final String key, final Object... args) {
		String val = WebUtil.initLocalizedMessage(key, null);
		if (args != null && args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				val = val.replaceAll("\\{" + i + "\\}", args[i].toString());
			}
		}
		return val;
	}

	public void validarCampos() throws ApplicationBusinessException {
		if (itemProcedHosp != null && itemProcedHosp.getPercServicoProfissional() != null) {
			if (itemProcedHosp.getPercServicoProfissional().floatValue() > 100.00) {
				throw new ApplicationBusinessException(ManterItemPrincipalControllerExceptionCode.PERCENTUAL_SERV_PROF_NAO_DEVE_SER_MAIOR_QUE_CEM);
			}
		}

		if (itemProcedHosp != null && itemProcedHosp.getIdadeMin() != null) {
			if (itemProcedHosp.getIdadeMin().intValue() < 0) {
				throw new ApplicationBusinessException(ManterItemPrincipalControllerExceptionCode.IDADE_MINIMA_DEVE_SER_MAIOR_IGUAL_ZERO);
			}
		}

		if (itemProcedHosp != null && itemProcedHosp.getIdadeMax() != null) {
			if (itemProcedHosp.getIdadeMax().intValue() < 0) {
				throw new ApplicationBusinessException(ManterItemPrincipalControllerExceptionCode.IDADE_MAXIMA_DEVE_SER_MAIOR_IGUAL_ZERO);
			}
		}

		if (itemProcedHosp.getIdadeMax().intValue() <= itemProcedHosp.getIdadeMin().intValue()) {
			throw new ApplicationBusinessException(ManterItemPrincipalControllerExceptionCode.IDADE_MAXIMA_DEVE_SER_MAIOR_IDADE_MINIMA);
		}
	}

	public String gravar() {

		try {
			this.validarCampos();

			itemProcedHosp.setClinica(clinica);
			itemProcedHosp.setTipoAto(tipoAto);
			itemProcedHosp.setTiposVinculo(tipoVinculo);
			itemProcedHosp.setMotivoCobrancaApac(motivoCobrancaAPAC);

			itemProcedHosp.setUtilizacaoItem(DominioUtilizacaoItem.A);

			if (grupo != null) {
				itemProcedHosp.setFogSgrGrpSeq(grupo.getSeq());
				itemProcedHosp.setGrupo(grupo);
			} else if (subGrupo != null) {
				itemProcedHosp.setFogSgrGrpSeq(null);
				itemProcedHosp.setGrupo(null);
			}

			if (subGrupo != null) {
				itemProcedHosp.setFogSgrSubGrupo(subGrupo.getId().getSubGrupo());
				itemProcedHosp.setSubGrupo(subGrupo);
			} else if (formaOrganizacao != null) {
				itemProcedHosp.setFogSgrSubGrupo(null);
				itemProcedHosp.setSubGrupo(null);
			}

			if (formaOrganizacao != null) {
				itemProcedHosp.setFogCodigo(formaOrganizacao.getId().getCodigo());
				itemProcedHosp.setFormaOrganizacao(formaOrganizacao);
			} else {
				itemProcedHosp.setFogCodigo(null);
				itemProcedHosp.setFormaOrganizacao(null);
			}

			itemProcedHosp.setSituacao(DominioSituacao.getInstance(situacao));

			if (procedimentoHospitalar != null) {
				itemProcedHosp.setProcedimentoHospitalar(procedimentoHospitalar);
				itemProcedHosp.getId().setPhoSeq(procedimentoHospitalar.getSeq());
			} else {
				itemProcedHosp.setProcedimentoHospitalar(null);
				itemProcedHosp.getId().setPhoSeq(null);
			}

			faturamentoFacade.persistirItemProcedimentoHospitalarComFlush(itemProcedHosp, itemProcedHospClone);

			if (seq != null) {
				apresentarMsgNegocio(Severity.INFO, ManterItemPrincipalControllerExceptionCode.ITEM_CONTA_HOSPITALAR_ALTERADA_SUCESSO.toString());
			} else {
				apresentarMsgNegocio(Severity.INFO, ManterItemPrincipalControllerExceptionCode.ITEM_CONTA_HOSPITALAR_INCLUIDA_SUCESSO.toString());
			}

			phoSeq = itemProcedHosp.getId().getPhoSeq();
			seq = itemProcedHosp.getId().getSeq();

			this.inicio();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	public void limparDependenciasSuggestionGrupo() {
		this.subGrupo = null;
		this.formaOrganizacao = null;
	}

	public void limparDependenciasSuggestionSubGrupo() {
		this.formaOrganizacao = null;
	}

	public String cancelar() {

		this.itemProcedHosp = null;
		this.itemProcedHospClone = null;
		this.clinica = null;
		this.procedimentoHospitalar = null;
		this.tipoAto = null;
		this.tipoVinculo = null;
		this.motivoCobrancaAPAC = null;
		this.listaCaractr = null;
		this.seq = null;
		this.phoSeq = null;
		this.situacao = null;
		this.grupo = null;
		this.subGrupo = null;
		this.formaOrganizacao = null;

		return "consultarItemTabelaList";
	}

	public String incluirProcedInt() {
		return "faturamento-manterProcedimentoHospitalarInternoList";
	}

	public String manterCarac() {
		return "manterCaracteristicasItens";
	}

	public String compatibilidade() {
		return "manterProcedHospitalarCompativel";
	}

	public String relacionarItem() {
		return "faturamento-relacionarPHIaSSM";
	}

	public String consultarPhiList() {
		return "faturamento-consultarPhiList";
	}

	public String relacionarCboComProcedimento() {
		return "relacionarCboComProcedimento";
	}

	public String valores() {
		return "manterValoresProcedimento";
	}
	public String complexidadeFinanciamento(){
		return "manterComplexidadeFinanciamento";
	}

	public List<FatFormaOrganizacao> listarFormasOrganizacaoPorCodigoOuDescricao(String objPesquisa) {
		return this.returnSGWithCount(this.faturamentoFacade.listarFormasOrganizacaoPorCodigoOuDescricao(objPesquisa, grupo.getSeq(), subGrupo.getId().getSubGrupo()),listarFormasOrganizacaoPorCodigoOuDescricaoCount(objPesquisa));
	}

	public Long listarFormasOrganizacaoPorCodigoOuDescricaoCount(String objPesquisa) {
		return this.faturamentoFacade.listarFormasOrganizacaoPorCodigoOuDescricaoCount(objPesquisa, grupo.getSeq(), subGrupo.getId().getSubGrupo());
	}

	public List<FatSubGrupo> listarSubGruposPorCodigoOuDescricao(String objPesquisa) {
		return this.returnSGWithCount(this.faturamentoFacade.listarSubGruposPorCodigoOuDescricao(objPesquisa, grupo.getSeq()),listarSubGruposPorCodigoOuDescricaoCount(objPesquisa));
	}

	public Long listarSubGruposPorCodigoOuDescricaoCount(String objPesquisa) {
		return this.faturamentoFacade.listarSubGruposPorCodigoOuDescricaoCount(objPesquisa, grupo.getSeq());
	}

	public List<FatGrupo> listarGruposPorCodigoOuDescricao(String objPesquisa) {
		return this.returnSGWithCount(this.faturamentoFacade.listarGruposPorCodigoOuDescricao(objPesquisa),listarGruposPorCodigoOuDescricaoCount(objPesquisa));
	}

	public Long listarGruposPorCodigoOuDescricaoCount(String objPesquisa) {
		return this.faturamentoFacade.listarGruposPorCodigoOuDescricaoCount(objPesquisa);
	}

	public List<FatProcedimentosHospitalares> pesquisarProcedimentos(String objPesquisa) {
		return this.returnSGWithCount(this.faturamentoFacade.listarProcedimentosHospitalaresPorSeqEDescricao(objPesquisa),pesquisarProcedimentosCount(objPesquisa));
	}

	public Long pesquisarProcedimentosCount(String objPesquisa) {
		return this.faturamentoFacade.listarProcedimentosHospitalaresPorSeqEDescricaoCount(objPesquisa);
	}

	public List<AghClinicas> pesquisarClinicas(String objPesquisa) {
		return this.returnSGWithCount(this.aghuFacade.pesquisarClinicasPorCodigoOuDescricao(objPesquisa, true, false),pesquisarClinicasCount(objPesquisa));
	}

	public Long pesquisarClinicasCount(String objPesquisa) {
		return this.aghuFacade.pesquisarClinicasPorCodigoOuDescricaoCount(objPesquisa);
	}

	public List<FatTipoAto> pesquisarTipoAto(Object objPesquisa) {
		return this.faturamentoFacade.pesquisarTipoAto(objPesquisa);
	}

	public Long pesquisarTipoAtoCount(Object objPesquisa) {
		return this.faturamentoFacade.pesquisarTipoAtoCount(objPesquisa);
	}

	public List<FatTiposVinculo> pesquisarTipoVinculo(Object objPesquisa) {
		return this.faturamentoFacade.pesquisarTipoVinculo(objPesquisa);
	}

	public Long pesquisarTipoVinculoCount(Object objPesquisa) {
		return this.faturamentoFacade.pesquisarTipoVinculoCount(objPesquisa);
	}

	public List<FatMotivoCobrancaApac> pesquisarMotivoCobrancaAPAC(String objPesquisa) {
		return this.returnSGWithCount(this.faturamentoFacade.pesquisarMotivoCobrancaApac(objPesquisa),pesquisarMotivoCobrancaAPACCount(objPesquisa));
	}

	public Long pesquisarMotivoCobrancaAPACCount(String objPesquisa) {
		return this.faturamentoFacade.pesquisarMotivoCobrancaApacCount(objPesquisa);
	}

	public FatItensProcedHospitalar getItemProcedHosp() {
		return itemProcedHosp;
	}

	public void setItemProcedHosp(FatItensProcedHospitalar itemProcedHosp) {
		this.itemProcedHosp = itemProcedHosp;
	}

	public AghClinicas getClinica() {
		return clinica;
	}

	public void setClinica(AghClinicas clinica) {
		this.clinica = clinica;
	}

	public FatProcedimentosHospitalares getProcedimentoHospitalar() {
		return procedimentoHospitalar;
	}

	public void setProcedimentoHospitalar(FatProcedimentosHospitalares procedimentoHospitalar) {
		this.procedimentoHospitalar = procedimentoHospitalar;
	}

	public FatTipoAto getTipoAto() {
		return tipoAto;
	}

	public void setTipoAto(FatTipoAto tipoAto) {
		this.tipoAto = tipoAto;
	}

	public FatTiposVinculo getTipoVinculo() {
		return tipoVinculo;
	}

	public void setTipoVinculo(FatTiposVinculo tipoVinculo) {
		this.tipoVinculo = tipoVinculo;
	}

	public FatMotivoCobrancaApac getMotivoCobrancaAPAC() {
		return motivoCobrancaAPAC;
	}

	public void setMotivoCobrancaAPAC(FatMotivoCobrancaApac motivoCobrancaAPAC) {
		this.motivoCobrancaAPAC = motivoCobrancaAPAC;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public Short getPhoSeq() {
		return phoSeq;
	}

	public void setPhoSeq(Short phoSeq) {
		this.phoSeq = phoSeq;
	}

	public List<FatCaractItemProcHosp> getListaCaractr() {
		return listaCaractr;
	}

	public void setListaCaractr(List<FatCaractItemProcHosp> listaCaractr) {
		this.listaCaractr = listaCaractr;
	}

	public Boolean getSituacao() {
		return situacao;
	}

	public void setSituacao(Boolean situacao) {
		this.situacao = situacao;
	}

	public FatGrupo getGrupo() {
		return grupo;
	}

	public void setGrupo(FatGrupo grupo) {
		this.grupo = grupo;
	}

	public FatSubGrupo getSubGrupo() {
		return subGrupo;
	}

	public void setSubGrupo(FatSubGrupo subGrupo) {
		this.subGrupo = subGrupo;
	}

	public FatFormaOrganizacao getFormaOrganizacao() {
		return formaOrganizacao;
	}

	public void setFormaOrganizacao(FatFormaOrganizacao formaOrganizacao) {
		this.formaOrganizacao = formaOrganizacao;
	}

    public static String getManterItemPrincipal() {
        return MANTER_ITEM_PRINCIPAL;
    }
}
