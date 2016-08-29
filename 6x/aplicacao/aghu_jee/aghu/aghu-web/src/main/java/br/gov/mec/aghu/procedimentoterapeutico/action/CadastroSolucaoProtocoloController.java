package br.gov.mec.aghu.procedimentoterapeutico.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang.StringUtils;
import org.primefaces.context.RequestContext;

import br.gov.mec.aghu.dominio.DominioCalculoDose;
import br.gov.mec.aghu.dominio.DominioUnidadeBaseParametroCalculo;
import br.gov.mec.aghu.model.AfaTipoVelocAdministracoes;
import br.gov.mec.aghu.model.AfaViaAdministracao;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.model.MptParamCalculoDoses;
import br.gov.mec.aghu.model.MptProtocoloItemMedicamentos;
import br.gov.mec.aghu.model.MptProtocoloMedicamentos;
import br.gov.mec.aghu.model.MptProtocoloMedicamentosDia;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ListaAfaDescMedicamentoTipoUsoMedicamentoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ParametroDoseUnidadeVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ProtocoloItensMedicamentoVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.view.VMpmDosagem;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.StringUtil;

public class CadastroSolucaoProtocoloController extends ActionController {
	private static final long serialVersionUID = 1316714145925020244L;
	private static final String CADASTRA_PROTOCOLO = "procedimentoterapeutico-cadastraProtocolo";
	private static final String TRES_ESPACOS = "   ";
	private static final String BARRA_M2 = "/m2";
	private static final String BARRA_KG = "/Kg";
	private static final String LABEL_DOSE_UNITARIA = "Dose Unitária";
	private static final String LABEL_DOSE_AUC = "AUC";
	private static final DominioCalculoDose MOSTELLER = DominioCalculoDose.M;
	private static final DominioCalculoDose CALVERT = DominioCalculoDose.C;
	private static final DominioCalculoDose DUBOIS = DominioCalculoDose.D;
	private static final DominioCalculoDose FIXO = DominioCalculoDose.F;
	private static final DominioCalculoDose PESO = DominioCalculoDose.G;
	private static final DominioCalculoDose HAYCOCK = DominioCalculoDose.H;
	@EJB
	private IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade;
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	private List<VMpmDosagem> listaComboUnidade;
	private List<VMpmDosagem> listaComboUnidadeParamDose = new ArrayList<VMpmDosagem>();
	private List<ProtocoloItensMedicamentoVO> listaMedicamentos = new ArrayList<ProtocoloItensMedicamentoVO>();
	private List<ProtocoloItensMedicamentoVO> listaMedicamentosExclusao = new ArrayList<ProtocoloItensMedicamentoVO>();
	private List<AfaTipoVelocAdministracoes> listaComboUnidadeInfusao;
	private List<MptProtocoloMedicamentosDia> listaMedDia = new ArrayList<MptProtocoloMedicamentosDia>();
	private List<ParametroDoseUnidadeVO> listaParam = new ArrayList<ParametroDoseUnidadeVO>();
	private List<ParametroDoseUnidadeVO> listaParamAux = new ArrayList<ParametroDoseUnidadeVO>();
	private AfaTipoVelocAdministracoes comboUnidadeInfusao;
	private MptProtocoloMedicamentos solucao = new MptProtocoloMedicamentos();
	private MptProtocoloMedicamentosDia itemDia = new MptProtocoloMedicamentosDia();
	private ProtocoloItensMedicamentoVO medicamento = new ProtocoloItensMedicamentoVO();
	private ProtocoloItensMedicamentoVO medicamentoEditado = new ProtocoloItensMedicamentoVO();
	private ProtocoloItensMedicamentoVO medicamentoSelecionado = new ProtocoloItensMedicamentoVO();
	private MptParamCalculoDoses paramDose = new MptParamCalculoDoses();
	private ParametroDoseUnidadeVO paramDoseVO = new ParametroDoseUnidadeVO();
	private ParametroDoseUnidadeVO paramDoseEditado = new ParametroDoseUnidadeVO();
	private ParametroDoseUnidadeVO parametroDoseSelecionado = new ParametroDoseUnidadeVO();
	private ListaAfaDescMedicamentoTipoUsoMedicamentoVO medicamentoSBAux = null;
	private Long codSolucao;
	private Long seqAux = (long) -1;
	private Short dias;
	private String observacao;
	private String unidadeBase;
	private String styleClass = StringUtils.EMPTY;
	private String fromBack;
	private String labelDoseUnitaria = LABEL_DOSE_UNITARIA;
	private Integer codVersaoProtocoloSessao;
	private Integer ordemProtocolo;
	private Integer seqNegativa = -1;
	private BigDecimal valorVolumeMl = BigDecimal.ZERO;
	private Boolean isEdicao = Boolean.FALSE;
	private Boolean isEdicaoMedicamento = Boolean.FALSE;
	private Boolean habilitarAdicionar = Boolean.FALSE;
	private Boolean todasVias = Boolean.FALSE;
	private Boolean isDiaModificado = Boolean.FALSE;
	private Boolean isEdicaoCalculoDose = Boolean.FALSE;
	private Boolean habilitarAdicionarParamDose = Boolean.FALSE;
	private Boolean diaMarcado = Boolean.FALSE;
	private Boolean readOnly;
	private Boolean obrigatorioComplemento = Boolean.FALSE;
	private Boolean modoVisualizacao = Boolean.FALSE;
	private Boolean desabilitarCampoUnidade = Boolean.FALSE;
	private String message;
	private boolean error;
	
	
	@PostConstruct
	public void init() {
		begin(conversation, true);
	}
	public void iniciar(){
		solucao.setVersaoProtocoloSessao(procedimentoTerapeuticoFacade.obterVersaoProtocoloSessaoPorSeq(codVersaoProtocoloSessao));
		if(isEdicao && codSolucao != null){
			buscarSolucaoParaEdicao(codSolucao);
			valorVolumeMl = solucao.getVolumeMl();
			observacao = solucao.getObservacao();
			buscarListaMedicamentos();
			listaMedDia = procedimentoTerapeuticoFacade.obterProtocoloMdtoDiaModificado(solucao.getSeq());	
		} else if(!isDiaModificado){
			solucao.setOrdem(ordemProtocolo.shortValue());
		}
		if(isDiaModificado){
			buscarSolucaoParaEdicao(codSolucao);
			buscarListaMedicamentos();
			preencherSolucaoDia();
		}
		preencherComboUnidadeInfusao();
		if(this.readOnly){
			this .habilitarAdicionarParamDose = this.readOnly;			
		}
	}
	public void preencherSolucaoDia(){
		MptProtocoloMedicamentosDia item = this.procedimentoTerapeuticoFacade.obterProtocoloMedicamentosDiaPorPtmSeqDiaCompleto(this.solucao.getSeq(), dias);
		solucao = new MptProtocoloMedicamentos();
		solucao.setComplemento(item.getComplemento());
		solucao.setDescricao(item.getDescricao());
		solucao.setDiasDeUsoDomiciliar(item.getDiasUsoDomiciliar());
		solucao.setFrequencia(item.getFrequencia());
		solucao.setGotejo(item.getGotejo());
		solucao.setIndBombaInfusao(item.getIndBombaInfusao());
		solucao.setIndDomiciliar(item.getIndUsoDomiciliar());
		solucao.setIndInfusorPortatil(item.getIndInfusorPortatil());
		solucao.setIndSeNecessario(item.getIndSeNecessario());
		solucao.setTempo(item.getTempo());
		solucao.setObservacao(item.getObservacao());
		if (item.getTvaSeq() != null) {
			solucao.setTipoVelocAdministracoes(procedimentoTerapeuticoFacade.obterUnidadeInfusao(item.getTvaSeq()));
		}
		solucao.setTfqSeq(procedimentoTerapeuticoFacade.obterAprazamento(item.getTfqSeq()));
		solucao.setIndSolucao(Boolean.TRUE);
		solucao.setVadSigla(procedimentoTerapeuticoFacade.obterVia(item.getVadSigla()));
		valorVolumeMl = item.getVolumeMl();
		if (item.getTvaSeq() != null) {
			comboUnidadeInfusao = procedimentoTerapeuticoFacade.obterUnidadeInfusao(item.getTvaSeq());
		}
		observacao = item.getObservacao();
		solucao.setVersaoProtocoloSessao(procedimentoTerapeuticoFacade.obterVersaoProtocoloSessaoPorSeq(codVersaoProtocoloSessao));
		solucao.setSeq(codSolucao);
		solucao.setQtdeHorasCorrer(item.getQtdHorasCorrer());
		solucao.setUnidHorasCorrer(item.getUnidHorasCorrer());
	}
	public void calcularValorVolumeMl(){
		this.valorVolumeMl = BigDecimal.ZERO;
		if (listaMedicamentos != null && !listaMedicamentos.isEmpty()) {
			for (ProtocoloItensMedicamentoVO item : this.listaMedicamentos) {
				this.valorVolumeMl = this.valorVolumeMl.add(procedimentoTerapeuticoFacade.calcularValorVolumeMl(item));
			}
		}
	}
	public void adicionarMedicamento(){
		if(listaMedicamentos != null){
			ProtocoloItensMedicamentoVO itemMedicamento = preencherItemMedicamento();
			if(!listaMedicamentos.isEmpty() && listaMedicamentos.contains(itemMedicamento) && !isEdicaoMedicamento){
				apresentarMsgNegocio(Severity.ERROR, "MS01_MPT_PROT_SOL_MED_EXISTENTE", itemMedicamento.getMedicamentoSb().getDescricaoEditada());
			} else {
				try {
					adicionarListaMedicamento(itemMedicamento);
					this.apresentarMsgNegocio("ADICIONAR_MEDICAMENTO_SUCESS");
				} catch (ApplicationBusinessException e) {
					this.apresentarExcecaoNegocio(e); 
				}	
			}
		}
	}
	public void alterarMedicamento(){
		if (medicamentoSelecionado != null 
				&& medicamentoSelecionado.getListaParametroCalculo() != null && !medicamentoSelecionado.getListaParametroCalculo().isEmpty() 
				&& medicamentoSBAux != null && !medicamentoSBAux.getMatCodigo().equals(medicamentoSelecionado.getMedMatCodigo())) {
			openDialog("modalExcluirDose");
		} else {
			confirmarAlteracaoMedicamento(false);
			exibirMesssage();
		}
	}
	private void adicionarListaMedicamento(ProtocoloItensMedicamentoVO itemMedicamento) throws ApplicationBusinessException {
		prescricaoMedicaFacade.validarCampoDose(medicamento.getDoseMedicamento());
		prescricaoMedicaFacade.verificarDosagemMedicamentoSolucao(itemMedicamento.getMedMatCodigo(), itemMedicamento.getPimDose(), itemMedicamento.getDosagem().getFormaDosagem().getSeq());
		listaMedicamentos.add(itemMedicamento);
		calcularValorVolumeMl();
		this.medicamento = new ProtocoloItensMedicamentoVO();
		this.medicamento.setMedicamentoSb(null);
		this.medicamentoSBAux = null;
	}
	private ProtocoloItensMedicamentoVO preencherItemMedicamento() {
		ProtocoloItensMedicamentoVO itemMedicamento = new ProtocoloItensMedicamentoVO();
		itemMedicamento.setMedicamentoSb(medicamento.getMedicamentoSb());
		itemMedicamento.setMedMatCodigo(medicamento.getMedicamentoSb().getMatCodigo());
		itemMedicamento.setVdmDescricao(medicamento.getMedicamentoSb().getDescricaoEditada());
		itemMedicamento.setPimDose(medicamento.getDoseMedicamento());
		itemMedicamento.setDosagem(medicamento.getComboUnidade());
		itemMedicamento.setComplementoMedicamento(medicamento.getComplementoMedicamento());
		itemMedicamento.setIndMedicamentoPadronizado(medicamento.getIndMedicamentoPadronizado());
		if(medicamento.getListaParametroCalculo() != null) {
			itemMedicamento.setListaParametroCalculo(medicamento.getListaParametroCalculo());
		}
		if(medicamento.getPimSeq() == null){
			itemMedicamento.setPimSeq(seqAux--);
		}else{
			itemMedicamento.setPimSeq(medicamento.getPimSeq());
		}
		if(medicamento.getComboUnidade() != null){
			itemMedicamento.setUmmDescricao(medicamento.getComboUnidade().getDescricaoUnidade());
		}
		return itemMedicamento;
	}
	public void selecionarMedicamento(){
		limparCamposParametro();
		paramDoseVO.setPmiSeq(medicamentoSelecionado.getPimSeq());
		paramDoseVO.setMedMatCodigo(medicamentoSelecionado.getMedMatCodigo());
		if(paramDoseVO.getTipoCalculo() != null && DominioCalculoDose.C.equals(paramDoseVO.getTipoCalculo())){
			paramDoseVO.setComboUnidade(null);
		} else {
			paramDoseVO.setComboUnidade(medicamentoSelecionado.getDosagem());
		}
		if (medicamentoSelecionado.getPimSeq() != null && medicamentoSelecionado.getPimSeq() > 0) {
			if (medicamentoSelecionado.getListaParametroCalculo() == null || medicamentoSelecionado.getListaParametroCalculo().isEmpty()) {
				medicamentoSelecionado.setListaParametroCalculo(procedimentoTerapeuticoFacade.preCarregarListaParametroDoseMedicamentoSolucao(medicamentoSelecionado));
			}
		}
		habilitarAdicionarParamDose = procedimentoTerapeuticoFacade.validarAdicaoParametroDoseParteUm(medicamentoSelecionado.getListaParametroCalculo(), paramDoseVO);
		preencherComboUnidadeParamDose(medicamentoSelecionado.getMedMatCodigo());
		//TODO
		if(listaParamAux != null && !listaParamAux.isEmpty()){
			if(listaParamAux.get(0).getMedMatCodigo().equals(medicamentoSelecionado.getMedMatCodigo())){
				medicamentoSelecionado.setListaParametroCalculo(listaParamAux);
			}
		}
		
	}
	public void adicionarMedicamentoCalculo(){
		if(medicamentoSelecionado == null){
			apresentarMsgNegocio(Severity.ERROR, "MSG_MEDICAMENTO_NAO_ASSOCIADO_DOSE");
		} else {
 			ParametroDoseUnidadeVO paramDose = new ParametroDoseUnidadeVO();
			paramDose.setTipoCalculo(paramDoseVO.getTipoCalculo());
			paramDose.setDose(paramDoseVO.getDose());
			paramDose.setComboUnidade(paramDoseVO.getComboUnidade());
			paramDose.setDoseMaximaUnitaria(paramDoseVO.getDoseMaximaUnitaria());
			paramDose.setIdadeMinima(paramDoseVO.getIdadeMinima());
			paramDose.setIdadeMaxima(paramDoseVO.getIdadeMaxima());
			if(paramDose.getIdadeMaxima() != null || paramDose.getIdadeMinima() != null){
				paramDose.setUnidIdade(paramDoseVO.getUnidIdade());
			}
			paramDose.setPesoMaximo(paramDoseVO.getPesoMaximo());
			paramDose.setPesoMinimo(paramDoseVO.getPesoMinimo());
			paramDose.setIsEdicao(paramDoseVO.getIsEdicao());
			if(paramDoseVO.getSeq() == null){
				paramDose.setSeq(seqNegativa--);
			} else {
				paramDose.setSeq(paramDoseVO.getSeq());
			}
			if(paramDoseVO.getMedMatCodigo() != null){
				paramDose.setMedMatCodigo(paramDoseVO.getMedMatCodigo());
			}
			preencherUnidadeBaseCalculo(paramDose);
			validarRegrasObrigatoriedade(paramDose);
		}
	}
	private void validarRegrasObrigatoriedade(ParametroDoseUnidadeVO paramDose) {
		try {
			procedimentoTerapeuticoFacade.validarCamposObrigatoriosParametroDose(paramDose, desabilitarCampoUnidade);
			habilitarAdicionarParamDose = procedimentoTerapeuticoFacade.validarAdicaoParametroDoseParteUm(medicamentoSelecionado.getListaParametroCalculo(), paramDose);
			procedimentoTerapeuticoFacade.validarPesoOuIdadeParamDose(medicamentoSelecionado.getListaParametroCalculo(), paramDose);
			procedimentoTerapeuticoFacade.validarPesoOuIdadeMinimoMaiorQueMaximo(paramDose);
			procedimentoTerapeuticoFacade.validarSobreposicaoDeFaixaParamDose(medicamentoSelecionado.getListaParametroCalculo(), paramDose);
			if(isEdicaoCalculoDose) {
				excluirCalculoDose(paramDoseEditado);
				isEdicaoCalculoDose = Boolean.FALSE;
				paramDose.setIsEdicao(Boolean.FALSE);
			}
			medicamentoSelecionado.getListaParametroCalculo().add(paramDose);
			limparCamposParametro();
		} catch (BaseListException e) {
			this.apresentarExcecaoNegocio(e);
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}
	private void preencherUnidadeBaseCalculo(ParametroDoseUnidadeVO paramDose) {
		if(paramDoseVO.getTipoCalculo() != null){
			if(MOSTELLER.equals(paramDoseVO.getTipoCalculo()) || 
					DUBOIS.equals(paramDoseVO.getTipoCalculo()) ||
					HAYCOCK.equals(paramDoseVO.getTipoCalculo())){
				paramDose.setUnidBaseCalculo(DominioUnidadeBaseParametroCalculo.M);
			}
			if(PESO.equals(paramDoseVO.getTipoCalculo())){
				paramDose.setUnidBaseCalculo(DominioUnidadeBaseParametroCalculo.K);
			}
			if(CALVERT.equals(paramDoseVO.getTipoCalculo())){
				paramDose.setUnidBaseCalculo(DominioUnidadeBaseParametroCalculo.A);
			}
			if(FIXO.equals(paramDoseVO.getTipoCalculo())){
				paramDose.setUnidBaseCalculo(DominioUnidadeBaseParametroCalculo.D);
			}
		}
	}
	public void renderizarUnidadeBase(){
		if(paramDoseVO != null && paramDoseVO.getTipoCalculo() != null){
			if(MOSTELLER.equals(paramDoseVO.getTipoCalculo()) || 
					DUBOIS.equals(paramDoseVO.getTipoCalculo()) ||
					HAYCOCK.equals(paramDoseVO.getTipoCalculo())){
				unidadeBase = BARRA_M2;
				desabilitarCampoUnidade = Boolean.FALSE;
				modificarLabelDoseUnitaria();
			}
			if(PESO.equals(paramDoseVO.getTipoCalculo())){
				unidadeBase = BARRA_KG;
				desabilitarCampoUnidade = Boolean.FALSE;
				modificarLabelDoseUnitaria();
			}
			if(CALVERT.equals(paramDoseVO.getTipoCalculo())){
				unidadeBase = TRES_ESPACOS;
				desabilitarCampoUnidade = Boolean.TRUE;
				paramDoseVO.setComboUnidade(null);
				modificarLabelDoseUnitaria();
			}
			if(FIXO.equals(paramDoseVO.getTipoCalculo())){
				unidadeBase = TRES_ESPACOS;
				desabilitarCampoUnidade = Boolean.FALSE;
				modificarLabelDoseUnitaria();
			}
		} else {
			unidadeBase = TRES_ESPACOS;
			desabilitarCampoUnidade = Boolean.FALSE;
			modificarLabelDoseUnitaria();
		}
	}
	public void modificarLabelDoseUnitaria(){
		if(this.paramDoseVO != null){
			if(this.paramDoseVO.getTipoCalculo() != null){
				if(DominioCalculoDose.C.equals(this.paramDoseVO.getTipoCalculo())){
					labelDoseUnitaria = LABEL_DOSE_AUC;
				} else {
					labelDoseUnitaria = LABEL_DOSE_UNITARIA;
				}
			} else {
				labelDoseUnitaria = LABEL_DOSE_UNITARIA;
			}
		} else {
			labelDoseUnitaria = LABEL_DOSE_UNITARIA;
		}
	}
	public void editarCalculoDose(ParametroDoseUnidadeVO item) {
		if(medicamentoSelecionado != null){
			paramDoseVO.setSeq(item.getSeq());
			paramDoseVO.setTipoCalculo(item.getTipoCalculo());
			paramDoseVO.setDose(item.getDose());
			paramDoseVO.setAfaFormaDosagem(item.getAfaFormaDosagem());
			paramDoseVO.setDoseMaximaUnitaria(item.getDoseMaximaUnitaria());
			paramDoseVO.setIdadeMinima(item.getIdadeMinima());
			paramDoseVO.setIdadeMaxima(item.getIdadeMaxima());
			paramDoseVO.setUnidIdade(item.getUnidIdade());
			paramDoseVO.setPesoMaximo(item.getPesoMaximo());
			paramDoseVO.setPesoMinimo(item.getPesoMinimo());
			paramDoseVO.setUnidBaseCalculo(item.getUnidBaseCalculo());
			paramDoseVO.setComboUnidade(item.getComboUnidade());
			paramDoseVO.setIsEdicao(Boolean.TRUE);
			renderizarUnidadeBase();
			isEdicaoCalculoDose = Boolean.TRUE;
			if(medicamentoSelecionado.getListaParametroCalculo().size() == 1){
				habilitarAdicionarParamDose = Boolean.FALSE;
			}
		}
	}
	public void excluirCalculoDose(ParametroDoseUnidadeVO item) {
		medicamentoSelecionado.getListaParametroCalculo().remove(item);
		if(isEdicao && item.getSeq() > 0){
			procedimentoTerapeuticoFacade.removerParametroDose(item);//XXX
		}
		if(medicamentoSelecionado.getListaParametroCalculo().isEmpty()){
			habilitarAdicionarParamDose = Boolean.FALSE;
		} else {
			habilitarAdicionarParamDose = procedimentoTerapeuticoFacade.validarAdicaoParametroDoseParteUm(medicamentoSelecionado.getListaParametroCalculo(), paramDoseVO);
		}
	}
	public void editarMedicamento(ProtocoloItensMedicamentoVO medicamentoGrid){
		this.isEdicaoMedicamento = Boolean.TRUE;
		limparEdicao();	
		ListaAfaDescMedicamentoTipoUsoMedicamentoVO  medicamentoSb = procedimentoTerapeuticoFacade.obterSbMedicamentos(medicamentoGrid.getMedMatCodigo(), medicamentoGrid.getVdmDescricao());
		if(medicamentoGrid.getMedicamentoSb() != null){
			medicamento.setMedicamentoSb(medicamentoGrid.getMedicamentoSb());
		}else{
			medicamento.setMedicamentoSb(medicamentoSb);
		}
		medicamentoSBAux = medicamento.getMedicamentoSb();
		if(!medicamentoGrid.getListaParametroCalculo().isEmpty()){
			medicamento.setListaParametroCalculo(medicamentoGrid.getListaParametroCalculo());
			listaParamAux = medicamentoGrid.getListaParametroCalculo();
		}
		if(medicamento.getMedicamentoSb() != null && medicamento.getMedicamentoSb().getMatCodigo() != null){
			medicamento.setMedMatCodigo(medicamento.getMedicamentoSb().getMatCodigo());
		}
		if(medicamentoGrid.getMedicamentoSb() != null && medicamentoGrid.getMedicamentoSb().getDescricaoEditada() != null){
			medicamento.setVdmDescricao(medicamentoGrid.getMedicamentoSb().getDescricaoEditada());
		}
		preencherComboUnidade();
		medicamento.setDoseMedicamento(medicamentoGrid.getPimDose());
		medicamento.setDosagem(medicamentoGrid.getDosagem());
		medicamento.setComboUnidade(medicamentoGrid.getDosagem());
		if(medicamentoGrid.getComplementoMedicamento() != null){
			medicamento.setComplementoMedicamento(medicamentoGrid.getComplementoMedicamento());
		}else{
			medicamento.setComplementoMedicamento(medicamentoGrid.getPimObservacao());
		}
		if(medicamentoGrid.getIndMedicamentoPadronizado() != null){
			medicamento.setIndMedicamentoPadronizado(medicamentoGrid.getIndMedicamentoPadronizado());
		}else if(medicamentoSb != null && medicamentoSb.getIndPadronizacao() != null){
			medicamento.setIndMedicamentoPadronizado(medicamentoSb.getIndPadronizacao());
		}
		for(ProtocoloItensMedicamentoVO s : this.listaMedicamentos){
			if(medicamentoGrid.getPimSeq().equals(s.getPimSeq())){
				s.setEdita(true);
			}
		}
		this.medicamentoEditado = medicamentoGrid;
		this.medicamentoSelecionado = medicamentoGrid;
	}
	public void limparListaParamDose(ProtocoloItensMedicamentoVO itemMedicamento){
		if(itemMedicamento.getListaParametroCalculo() != null && !itemMedicamento.getListaParametroCalculo().isEmpty()){
			for(ParametroDoseUnidadeVO objParam : itemMedicamento.getListaParametroCalculo()){
				if(objParam.getSeq() > 0){
					procedimentoTerapeuticoFacade.excluirParametroDoseMedicamento(objParam.getSeq());
				}
			}
			if(itemMedicamento.getPimSeq() > 0){
				procedimentoTerapeuticoFacade.excluirProtocoloSolucaoPorSeq(itemMedicamento.getPimSeq());
			}
		}
		habilitarAdicionarParamDose = procedimentoTerapeuticoFacade.validarAdicaoParametroDoseParteUm(itemMedicamento.getListaParametroCalculo(), paramDoseVO);
		itemMedicamento.setListaParametroCalculo(new ArrayList<ParametroDoseUnidadeVO>());
		this.listaParamAux = new ArrayList<ParametroDoseUnidadeVO>();
	}
	public void preencherSuggestionboxMedicamentoPosAlteracao(){
		habilitarAdicionarParamDose = procedimentoTerapeuticoFacade.validarAdicaoParametroDoseParteUm(medicamentoSelecionado.getListaParametroCalculo(), paramDoseVO);
	}
	public void excluirMedicamento(ProtocoloItensMedicamentoVO medicamento){
		if(medicamento.getPimSeq() > 0){
			listaMedicamentosExclusao.add(medicamento);
		}
		listaMedicamentos.remove(medicamento);
		limparUnidadeParamDose();
		limparListaParamDose(medicamento);
		medicamentoSelecionado = new ProtocoloItensMedicamentoVO();
		medicamentoSBAux = null;
		listaParamAux = null;
		calcularValorVolumeMl();
	}
	public void cancelarEdicao(){
		if(listaParamAux != null && !listaParamAux.isEmpty()){
			medicamentoSelecionado.setListaParametroCalculo(listaParamAux);
		}
		limparFieldsetMedicamento();
	}
	public void cancelarEdicaoParametro(){
		limparCamposParametro();
	}
	public void limparUnidadeParamDose(){
		this.paramDoseVO.setComboUnidade(new VMpmDosagem());
		this.listaComboUnidadeParamDose = new ArrayList<VMpmDosagem>();
	}
	public void limparCamposParametro() {
//		this.paramDoseVO = new ParametroDoseUnidadeVO();
		this.paramDoseVO.setAfaFormaDosagem(null);
		this.paramDoseVO.setAfaFormaDosagemVO(null);
		this.paramDoseVO.setAlertaCalculoDose(null);
		this.paramDoseVO.setComboUnidade(null);
		this.paramDoseVO.setDescricao(null);
		this.paramDoseVO.setDose(null);
		this.paramDoseVO.setDoseMaximaUnitaria(null);
		this.paramDoseVO.setFdsSeq(null);
		this.paramDoseVO.setIdadeMaxima(null);
		this.paramDoseVO.setIdadeMinima(null);
		this.paramDoseVO.setMptProtocoloItemMedicamentos(null);
		this.paramDoseVO.setPesoMaximo(null);
		this.paramDoseVO.setPesoMinimo(null);
		this.paramDoseVO.setPmiSeq(null);
		this.paramDoseVO.setSeq(null);
		this.paramDoseVO.setTipoCalculo(null);
		this.paramDoseVO.setUnidBaseCalculo(null);
		this.paramDoseVO.setUnidIdade(null);
		
		this.isEdicaoCalculoDose = Boolean.FALSE;
		this.unidadeBase = TRES_ESPACOS;
		this.parametroDoseSelecionado = new ParametroDoseUnidadeVO();
		this.desabilitarCampoUnidade = Boolean.FALSE;
		this.labelDoseUnitaria = LABEL_DOSE_UNITARIA;
		habilitarAdicionarParamDose = procedimentoTerapeuticoFacade.validarAdicaoParametroDoseParteUm(medicamentoSelecionado.getListaParametroCalculo(), paramDoseVO);
	}
	public void calcularTempo(){
		if(solucao != null && solucao.getQtdeHorasCorrer() != null && solucao.getUnidHorasCorrer() != null){
			Calendar tempo = GregorianCalendar.getInstance();
			DateUtil.zeraHorario(tempo);
			switch (this.solucao.getUnidHorasCorrer()) {
				case H:
					tempo.set(Calendar.HOUR_OF_DAY, this.solucao.getQtdeHorasCorrer().intValue());
					break;
				case M:
					tempo.set(Calendar.HOUR_OF_DAY, 0);
					tempo.set(Calendar.MINUTE, this.solucao.getQtdeHorasCorrer().intValue());
					break;
				default:
					break;
			}
			this.solucao.setTempo(tempo.getTime());
		}
	}
	public void gravar(){
		preencheCamposDaTela();
		if(this.solucao.getObservacao() != null && this.solucao.getObservacao().length() > 240){
			this.apresentarMsgNegocio(Severity.ERROR, "MSG_TAMANHO_CAMPO_OBSERVACAO_SOLUCAO");
		}else{
			try {
			procedimentoTerapeuticoFacade.verificarInsercaoSolucao(solucao, listaMedicamentos);
			if(isEdicao){
				if(this.verificarRN09()){
					this.apresentarMsgNegocio(Severity.INFO, "MPT_PROT_SOL_ALTERADO_SUCESSO");
				}
			}else{
					RapServidores servidor = registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado());
					solucao.setCriadoEm(new Date());
					solucao.setServidor(servidor);
					solucao.setIndSolucao(Boolean.TRUE);
					if(solucao.getIndNaoPermiteAlteracao() == null){
						solucao.setIndNaoPermiteAlteracao(Boolean.FALSE);
					}
					procedimentoTerapeuticoFacade.persistirSolucao(solucao);
					for(ProtocoloItensMedicamentoVO obj : listaMedicamentos){
						MptProtocoloItemMedicamentos itemMedicamento = new MptProtocoloItemMedicamentos();
						itemMedicamento.setMedMatCodigo(obj.getMedMatCodigo());
						itemMedicamento.setProtocoloMedicamentos(solucao);
						itemMedicamento.setServidor(servidor);
						itemMedicamento.setCriadoEm(new Date());
						itemMedicamento.setDose(obj.getPimDose());
						itemMedicamento.setAfaFormaDosagem(procedimentoTerapeuticoFacade.obterAfaFormaDosagemPorChavePrimaria(obj.getDosagem().getSeqDosagem()));
						itemMedicamento.setObservacao(obj.getComplementoMedicamento());
						procedimentoTerapeuticoFacade.persistirItemMedicamentoSolucao(itemMedicamento);
						procedimentoTerapeuticoFacade.desatacharMptProtocoloMdto(solucao);
						for(ParametroDoseUnidadeVO objCalculo : obj.getListaParametroCalculo()){
							if(objCalculo.getMedMatCodigo() == null){
								apresentarMsgNegocio(Severity.ERROR, "MSG_MEDICAMENTO_NAO_ASSOCIADO_DOSE");
								procedimentoTerapeuticoFacade.excluirProtocoloItemMedicamentoDaSolucao(solucao.getSeq());
								solucao.setSeq(null);
								return;
							} else {
								if(objCalculo.getMedMatCodigo().equals(obj.getMedMatCodigo())){
									MptParamCalculoDoses calculoDose = new MptParamCalculoDoses();
									calculoDose.setMptProtocoloItemMedicamentos(itemMedicamento);
									calculoDose.setServidor(servidor);
									calculoDose.setCriadoEm(new Date());
									calculoDose.setIdadeMaxima(objCalculo.getIdadeMaxima());
									calculoDose.setIdadeMinima(objCalculo.getIdadeMinima());
									calculoDose.setPesoMaximo(objCalculo.getPesoMaximo());
									calculoDose.setPesoMinimo(objCalculo.getPesoMinimo());
									calculoDose.setTipoCalculo(objCalculo.getTipoCalculo());
									calculoDose.setDose(objCalculo.getDose());
									calculoDose.setDoseMaximaUnitaria(objCalculo.getDoseMaximaUnitaria());
									calculoDose.setUnidBaseCalculo(objCalculo.getUnidBaseCalculo());
									if(objCalculo.getComboUnidade() != null){
										calculoDose.setAfaFormaDosagem(procedimentoTerapeuticoFacade.obterAfaFormaDosagemPorChavePrimaria(objCalculo.getComboUnidade().getSeqDosagem()));
									}
									calculoDose.setUnidIdade(objCalculo.getUnidIdade());
									procedimentoTerapeuticoFacade.preInserirParametroDoseMedicamentoSolucao(calculoDose);
									if(objCalculo.getSeq() == null || objCalculo.getSeq() < 0){
										objCalculo.setSeq(null);
										procedimentoTerapeuticoFacade.persistirParametroDoseMedicamentoSolucao(calculoDose);
									} else {
										procedimentoTerapeuticoFacade.atualizarParametroDoseMedicamentoSolucao(calculoDose);
									}
								}
							}
						}
					}
					this.apresentarMsgNegocio(Severity.INFO, "MS08_MPT_PROT_SOL_INSERIDO_SUCESSO");
					limpar();
					limparUnidadeParamDose();
					this.isEdicaoCalculoDose = Boolean.FALSE;
					habilitarAdicionarParamDose = Boolean.FALSE;
				}
			} catch (ApplicationBusinessException e) {
				this.apresentarExcecaoNegocio(e); 
			}
		}
	}
	public void gravarDia(){
		itemDia = this.procedimentoTerapeuticoFacade.obterProtocoloMedicamentosDiaPorPtmSeqDiaCompleto(this.solucao.getSeq(), dias);
   		itemDia.setComplemento(this.solucao.getComplemento());
       	itemDia.setDescricao(this.solucao.getDescricao());
       	itemDia.setDiasUsoDomiciliar(this.solucao.getDiasDeUsoDomiciliar());
   		itemDia.setFrequencia(this.solucao.getFrequencia());
   		itemDia.setGotejo(this.solucao.getGotejo());
    	itemDia.setIndBombaInfusao(this.solucao.getIndBombaInfusao());
    	itemDia.setIndInfusorPortatil(this.solucao.getIndInfusorPortatil());
    	itemDia.setIndSeNecessario(this.solucao.getIndSeNecessario());
    	itemDia.setIndSolucao(this.solucao.getIndSolucao());
    	itemDia.setIndUsoDomiciliar(this.solucao.getIndDomiciliar());
    	itemDia.setQtdHorasCorrer(this.solucao.getQtdeHorasCorrer());
    	itemDia.setProtocoloMedicamentos(this.solucao);
    	itemDia.setTempo(this.solucao.getTempo());
    	itemDia.setTfqSeq(this.solucao.getTfqSeq().getSeq());
    	if (this.solucao.getTipoVelocAdministracoes() != null) {
    		itemDia.setTvaSeq(this.solucao.getTipoVelocAdministracoes().getSeq());
    	}
    	itemDia.setUnidHorasCorrer(this.solucao.getUnidHorasCorrer());
    	itemDia.setVadSigla(this.solucao.getVadSigla().getSigla());
    	itemDia.setVersaoProtocoloSessao(this.solucao.getVersaoProtocoloSessao());
    	itemDia.setModificado(Boolean.TRUE);
    	itemDia.setObservacao(observacao);
    	itemDia.setVolumeMl(valorVolumeMl);
    	if (comboUnidadeInfusao != null) {
    		itemDia.setTvaSeq(comboUnidadeInfusao.getSeq());
    	}
    	itemDia.setProtocoloMedicamentos(procedimentoTerapeuticoFacade.buscarSolucaoParaEdicao(codSolucao));
        this.procedimentoTerapeuticoFacade.atualizarProtocoloSolucaoDia(itemDia);
        itemDia = new MptProtocoloMedicamentosDia();
		limpar();
		limparUnidadeParamDose();
		habilitarAdicionarParamDose = Boolean.FALSE;
		this.isEdicaoCalculoDose = Boolean.FALSE;
	}
	private void preencheCamposDaTela() {
		if(comboUnidadeInfusao != null){
			solucao.setTipoVelocAdministracoes(comboUnidadeInfusao);
		}else{
			solucao.setTipoVelocAdministracoes(null);
		}
		if(valorVolumeMl != null){
			solucao.setVolumeMl(valorVolumeMl);
		}
		if(observacao != null){
			solucao.setObservacao(StringUtil.trim(observacao));
		}
	}
	public void confirmarAlteracao() {
		confirmarAlteracaoMedicamento(true);
	}
	
	public void exibirMesssage() {
		if(error){
			apresentarMsgNegocio(Severity.ERROR, message);
		}else{
			this.apresentarMsgNegocio("EDITAR_MEDICAMENTO_SUCESS");
		}
	}
	
	public void confirmarAlteracaoMedicamento(boolean limparLista) {
		if(listaMedicamentos != null){
			ProtocoloItensMedicamentoVO itemMedicamento = preencherItemMedicamento();
			if (!listaMedicamentos.isEmpty() && listaMedicamentos.contains(itemMedicamento) 
					&& !medicamentoEditado.getMedMatCodigo().equals(itemMedicamento.getMedMatCodigo())) {
				message =  "Medicamento "+ itemMedicamento.getMedicamentoSb().getDescricaoEditada() + " já adicionado a solução.";
				error = true;
			} else {
				try {
					if (limparLista) {
						limparListaParamDose(itemMedicamento);
					}
					adicionarListaMedicamento(itemMedicamento);
					this.listaMedicamentos.remove(medicamentoEditado);
					this.isEdicaoMedicamento = Boolean.FALSE;
					this.medicamentoEditado = new ProtocoloItensMedicamentoVO();
					this.listaParamAux = new ArrayList<ParametroDoseUnidadeVO>();//TODO
					error = false;
				} catch (ApplicationBusinessException e) {
					this.apresentarExcecaoNegocio(e); 
				}	
			}
		} 
		closeDialog("modalExcluirDose");
 	}
	public void cancelarAlteracaoMedicamento() {
		medicamento.setMedicamentoSb(medicamentoSBAux);
		if(listaParamAux != null && !listaParamAux.isEmpty()){
			medicamentoSelecionado.setListaParametroCalculo(listaParamAux);
		}
	}
	public void alterarTodosDiasModificados(){
		for(MptProtocoloMedicamentosDia item: listaMedDia){
        	if(item.getModificado()){
	       		item.setComplemento(this.solucao.getComplemento());
	       		item.setCriadoEm(this.solucao.getCriadoEm());
	           	item.setDescricao(this.solucao.getDescricao());
	           	item.setDiasUsoDomiciliar(this.solucao.getDiasDeUsoDomiciliar());
           		item.setFrequencia(this.solucao.getFrequencia());
           		item.setGotejo(this.solucao.getGotejo());
            	item.setIndBombaInfusao(this.solucao.getIndBombaInfusao());
            	item.setIndInfusorPortatil(this.solucao.getIndInfusorPortatil());
            	item.setIndSeNecessario(this.solucao.getIndSeNecessario());
            	item.setIndSolucao(this.solucao.getIndSolucao());
            	item.setIndUsoDomiciliar(this.solucao.getIndDomiciliar());
            	item.setModificado(Boolean.FALSE);
            	item.setObservacao(this.solucao.getObservacao());
            	item.setQtdHorasCorrer(this.solucao.getQtdeHorasCorrer());
            	item.setServidor(this.solucao.getServidor());
            	item.setProtocoloMedicamentos(this.solucao);
            	item.setTempo(this.solucao.getTempo());
            	item.setTfqSeq(this.solucao.getTfqSeq().getSeq());
            	if (this.solucao.getTipoVelocAdministracoes() != null) {
            		item.setTvaSeq(this.solucao.getTipoVelocAdministracoes().getSeq());
        		}
            	item.setUnidHorasCorrer(this.solucao.getUnidHorasCorrer());
            	item.setVadSigla(this.solucao.getVadSigla().getSigla());
            	item.setVersaoProtocoloSessao(this.solucao.getVersaoProtocoloSessao());
            	item.setVolumeMl(this.solucao.getVolumeMl());
                this.procedimentoTerapeuticoFacade.atualizarProtocoloSolucaoDia(item);
             }
         }
		limpar();
		limparUnidadeParamDose();
		this.isEdicaoCalculoDose = Boolean.FALSE;
		habilitarAdicionarParamDose = Boolean.FALSE;
     }
	 public void alterarDiasModificados(){
         for(MptProtocoloMedicamentosDia item: listaMedDia){
             if(!item.getModificado()){
              	 item.setComplemento(this.solucao.getComplemento());
            	 item.setCriadoEm(this.solucao.getCriadoEm());
            	 item.setDescricao(this.solucao.getDescricao());
            	 item.setDiasUsoDomiciliar(this.solucao.getDiasDeUsoDomiciliar());
            	 item.setFrequencia(this.solucao.getFrequencia());
            	 item.setGotejo(this.solucao.getGotejo());
            	 item.setIndBombaInfusao(this.solucao.getIndBombaInfusao());
            	 item.setIndInfusorPortatil(this.solucao.getIndInfusorPortatil());
            	 item.setIndSeNecessario(this.solucao.getIndSeNecessario());
            	 item.setIndSolucao(this.solucao.getIndSolucao());
            	 item.setIndUsoDomiciliar(this.solucao.getIndDomiciliar());
            	 item.setObservacao(this.solucao.getObservacao());
            	 item.setQtdHorasCorrer(this.solucao.getQtdeHorasCorrer());
            	 item.setServidor(this.solucao.getServidor());
            	 item.setProtocoloMedicamentos(this.solucao);
            	 item.setTempo(this.solucao.getTempo());
            	 item.setTfqSeq(this.solucao.getTfqSeq().getSeq());
            	 if (this.solucao.getTipoVelocAdministracoes() != null) {
            		 item.setTvaSeq(this.solucao.getTipoVelocAdministracoes().getSeq());
            	 }
            	 item.setUnidHorasCorrer(this.solucao.getUnidHorasCorrer());
            	 item.setVadSigla(this.solucao.getVadSigla().getSigla());
            	 item.setVersaoProtocoloSessao(this.solucao.getVersaoProtocoloSessao());
            	 item.setVolumeMl(this.solucao.getVolumeMl());                       
                 this.procedimentoTerapeuticoFacade.atualizarProtocoloSolucaoDia(item);                     
             }
         }
 		limpar();
 		limparUnidadeParamDose();
 		this.isEdicaoCalculoDose = Boolean.FALSE;
 		habilitarAdicionarParamDose = Boolean.FALSE;
	}
	public Boolean verificarRN09(){
		Boolean testarRetorno = Boolean.TRUE;
		try {
			procedimentoTerapeuticoFacade.alterarSolucao(solucao, listaMedicamentos, listaMedicamentosExclusao);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		if(listaMedDia != null){
			for(MptProtocoloMedicamentosDia item : listaMedDia){
                if(item.getModificado()){
                	testarRetorno = Boolean.FALSE;
                	RequestContext.getCurrentInstance().execute("PF('modal_dias_modificados').show()");
                	break;
                }
            }
			if(testarRetorno){				
				return testarRetorno;
			}
        } else {
        	return testarRetorno;
        }
		return testarRetorno;
	}
	private void limparFieldsetMedicamento() {
		this.medicamento = new ProtocoloItensMedicamentoVO();
		this.medicamentoSBAux = null;
		listaParamAux = null;
		this.isEdicaoMedicamento = Boolean.FALSE;
		limparEdicao();
	}
	public void buscarSolucaoParaEdicao(Long codSolucao) {
		solucao = procedimentoTerapeuticoFacade.buscarSolucaoParaEdicao(codSolucao);
		if(solucao != null && solucao.getObservacao() != null){
			this.setObservacao(solucao.getObservacao() );
		}
		if (solucao.getTvaSeq() != null) {
			comboUnidadeInfusao = procedimentoTerapeuticoFacade.obterUnidadeInfusao(solucao.getTvaSeq());
		}
	}
	public void buscarListaMedicamentos(){
		listaMedicamentos = procedimentoTerapeuticoFacade.listarItensMedicamentoProtocolo(codSolucao);
	}
	public void preencherComboUnidade(){
		medicamentoSBAux = medicamento.getMedicamentoSb();
		listaComboUnidade = procedimentoTerapeuticoFacade.listarComboUnidade(medicamento.getMedicamentoSb().getMatCodigo());
		
	}
	public void preencherComboUnidadeParamDose(Integer matCodigo){
		listaComboUnidadeParamDose = procedimentoTerapeuticoFacade.listarComboUnidade(matCodigo);
	}
	public void preencherComboUnidadeInfusao(){
		listaComboUnidadeInfusao = procedimentoTerapeuticoFacade.listarComboUnidadeInfusao();
	}
	public String voltar(){
		limpar();
		limparUnidadeParamDose();
		this.isEdicaoCalculoDose = Boolean.FALSE;
		habilitarAdicionarParamDose = Boolean.FALSE;
		return CADASTRA_PROTOCOLO;
	}
	private void limpar() {
		solucao = new MptProtocoloMedicamentos();
		medicamento = new ProtocoloItensMedicamentoVO();
		listaMedicamentos = new ArrayList<ProtocoloItensMedicamentoVO>();
		comboUnidadeInfusao = new AfaTipoVelocAdministracoes();
		paramDoseVO = new ParametroDoseUnidadeVO();
		listaParam = new ArrayList<ParametroDoseUnidadeVO>();
		valorVolumeMl = BigDecimal.ZERO;
		observacao = StringUtils.EMPTY;
		isEdicao = Boolean.FALSE;
		solucao.setVersaoProtocoloSessao(procedimentoTerapeuticoFacade.obterVersaoProtocoloSessaoPorSeq(codVersaoProtocoloSessao));
		todasVias = Boolean.FALSE;
		isEdicaoMedicamento = Boolean.FALSE;
		medicamentoSelecionado = new ProtocoloItensMedicamentoVO();
		parametroDoseSelecionado = new ParametroDoseUnidadeVO();
		listaParamAux = new ArrayList<ParametroDoseUnidadeVO>();
	}
	private void limparEdicao(){
		for(ProtocoloItensMedicamentoVO s : this.listaMedicamentos){
			s.setEdita(false);
		}
	}
	public String getDescricaoAprazamento() {
		if (solucao != null && solucao.getTfqSeq() != null) {
			if (solucao.getTfqSeq().getIndDigitaFrequencia()) {
				return solucao.getTfqSeq().getSintaxeFormatada(solucao.getFrequencia());
			} else {
				return solucao.getTfqSeq().getDescricao();
			}
		}
		return StringUtils.EMPTY;
	}
	public List<ListaAfaDescMedicamentoTipoUsoMedicamentoVO> listarSuggestionboxMedicamentos (String param){
		return returnSGWithCount(procedimentoTerapeuticoFacade.listarSbMedicamentos(param, medicamento.getIndMedicamentoPadronizado()), procedimentoTerapeuticoFacade.listarSbMedicamentosCount(param, medicamento.getIndMedicamentoPadronizado()));
	}
	public List<AfaViaAdministracao> listarSuggestionboxVias(String param){
		return returnSGWithCount(procedimentoTerapeuticoFacade.listarSbViaAdministracaoMedicamento(param, this.listaMedicamentos, todasVias), procedimentoTerapeuticoFacade.listarSbViaAdministracaoMedicamentoCount(param, this.listaMedicamentos, todasVias));
	}
	public List<MpmTipoFrequenciaAprazamento> listarSuggestionTipoFrequenciaAprazamento(String param){
		return returnSGWithCount(procedimentoTerapeuticoFacade.listarSuggestionTipoFrequenciaAprazamento(param), procedimentoTerapeuticoFacade.listarSuggestionTipoFrequenciaAprazamentoCount(param));
	}
	public ProtocoloItensMedicamentoVO getMedicamento() {		return medicamento;
	}
	public void setMedicamento(ProtocoloItensMedicamentoVO medicamento) {		this.medicamento = medicamento;
	}
	public MptProtocoloMedicamentos getSolucao() {		return solucao;
	}
	public void setSolucao(MptProtocoloMedicamentos solucao) {		this.solucao = solucao;
	}
	public List<ProtocoloItensMedicamentoVO> getListaMedicamentos() {		return listaMedicamentos;
	}
	public void setListaMedicamentos(List<ProtocoloItensMedicamentoVO> listaMedicamentos) {		this.listaMedicamentos = listaMedicamentos;
	}
	public List<VMpmDosagem> getListaComboUnidade() {		return listaComboUnidade;
	}
	public void setListaComboUnidade(List<VMpmDosagem> listaComboUnidade) {		this.listaComboUnidade = listaComboUnidade;
	}
	public List<AfaTipoVelocAdministracoes> getListaComboUnidadeInfusao() {		return listaComboUnidadeInfusao;
	}
	public void setListaComboUnidadeInfusao(List<AfaTipoVelocAdministracoes> listaComboUnidadeInfusao) {		this.listaComboUnidadeInfusao = listaComboUnidadeInfusao;
	}
	public AfaTipoVelocAdministracoes getComboUnidadeInfusao() {		return comboUnidadeInfusao;
	}
	public void setComboUnidadeInfusao(AfaTipoVelocAdministracoes comboUnidadeInfusao) {		this.comboUnidadeInfusao = comboUnidadeInfusao;
	}
	public Long getCodSolucao() {		return codSolucao;
	}
	public void setCodSolucao(Long codSolucao) {		this.codSolucao = codSolucao;
	}
	public Boolean getIsEdicao() {		return isEdicao;
	}
	public void setIsEdicao(Boolean isEdicao) {		this.isEdicao = isEdicao;
	}
	public Boolean getIsEdicaoMedicamento() {		return isEdicaoMedicamento;
	}
	public void setIsEdicaoMedicamento(Boolean isEdicaoMedicamento) {		this.isEdicaoMedicamento = isEdicaoMedicamento;
	}
	public Boolean getTodasVias() {		return todasVias;
	}
	public void setTodasVias(Boolean todasVias) {		this.todasVias = todasVias;
	}
	public Boolean getHabilitarAdicionar() {		return habilitarAdicionar;
	}
	public void setHabilitarAdicionar(Boolean habilitarAdicionar) {		this.habilitarAdicionar = habilitarAdicionar;
	}
	public String getObservacao() {	return observacao;
	}
	public void setObservacao(String observacao) {	this.observacao = observacao;
	}
	public BigDecimal getValorVolumeMl() {	return valorVolumeMl;
	}
	public void setValorVolumeMl(BigDecimal valorVolumeMl) {	this.valorVolumeMl = valorVolumeMl;
	}
	public Integer getCodVersaoProtocoloSessao() {	return codVersaoProtocoloSessao;
	}
	public void setCodVersaoProtocoloSessao(Integer codVersaoProtocoloSessao) {	this.codVersaoProtocoloSessao = codVersaoProtocoloSessao;
	}
	public Integer getOrdemProtocolo() {	return ordemProtocolo;
	}
	public void setOrdemProtocolo(Integer ordemProtocolo) {	this.ordemProtocolo = ordemProtocolo;
	}
	public List<MptProtocoloMedicamentosDia> getListaMedDia() {	return listaMedDia;
	}
	public void setListaMedDia(List<MptProtocoloMedicamentosDia> listaMedDia) {	this.listaMedDia = listaMedDia;
	}
	public Short getDias() {	return dias;
	}
	public void setDias(Short dias) {	this.dias = dias;
	}
	public Boolean getIsDiaModificado() {	return isDiaModificado;
	}
	public void setIsDiaModificado(Boolean isDiaModificado) {	this.isDiaModificado = isDiaModificado;
	}
	public List<ParametroDoseUnidadeVO> getListaParam() {	return listaParam;
	}
	public void setListaParam(List<ParametroDoseUnidadeVO> listaParam) {	this.listaParam = listaParam;
	}
	public Boolean getIsEdicaoCalculoDose() {	return isEdicaoCalculoDose;
	}
	public void setIsEdicaoCalculoDose(Boolean isEdicaoCalculoDose) {	this.isEdicaoCalculoDose = isEdicaoCalculoDose;
	}
	public ProtocoloItensMedicamentoVO getMedicamentoEditado() {	return medicamentoEditado;
	}
	public void setMedicamentoEditado(ProtocoloItensMedicamentoVO medicamentoEditado) {	this.medicamentoEditado = medicamentoEditado;
	}
	public MptProtocoloMedicamentosDia getItemDia() {	return itemDia;
	}
	public void setItemDia(MptProtocoloMedicamentosDia itemDia) {	this.itemDia = itemDia;
	}
	public Boolean getDiaMarcado() {	return diaMarcado;
	}
	public void setDiaMarcado(Boolean diaMarcado) {	this.diaMarcado = diaMarcado;
	}
	public ProtocoloItensMedicamentoVO getMedicamentoSelecionado() {	return medicamentoSelecionado;	}
	public void setMedicamentoSelecionado(ProtocoloItensMedicamentoVO medicamentoSelecionado) {	this.medicamentoSelecionado = medicamentoSelecionado;	}
	public MptParamCalculoDoses getParamDose() {	return paramDose;	}
	public void setParamDose(MptParamCalculoDoses paramDose) {	this.paramDose = paramDose;	}
	public Boolean getHabilitarAdicionarParamDose() {	return habilitarAdicionarParamDose;	}
	public void setHabilitarAdicionarParamDose(Boolean habilitarAdicionarParamDose) {	this.habilitarAdicionarParamDose = habilitarAdicionarParamDose;	}
	public ParametroDoseUnidadeVO getParamDoseVO() {	return paramDoseVO;	}
	public void setParamDoseVO(ParametroDoseUnidadeVO paramDoseVO) {	this.paramDoseVO = paramDoseVO;	}
	public String getUnidadeBase() {	return unidadeBase;	}
	public void setUnidadeBase(String unidadeBase) {	this.unidadeBase = unidadeBase;	}
	public Boolean getReadOnly() {	return readOnly;	}
	public void setReadOnly(Boolean readOnly) {	this.readOnly = readOnly;	}
	public String getFromBack() {	return fromBack;	}
	public void setFromBack(String fromBack) {	this.fromBack = fromBack;	}
	public Boolean getModoVisualizacao() {	return modoVisualizacao;	}
	public void setModoVisualizacao(Boolean modoVisualizacao) {	this.modoVisualizacao = modoVisualizacao;	}
	public Boolean getObrigatorioComplemento() {	return obrigatorioComplemento;	}
	public void setObrigatorioComplemento(Boolean obrigatorioComplemento) {	this.obrigatorioComplemento = obrigatorioComplemento;	}
	public String getStyleClass() {	return styleClass;	}
	public void setStyleClass(String styleClass) {	this.styleClass = styleClass;	}
	public ParametroDoseUnidadeVO getParamDoseEditado() {	return paramDoseEditado;	}
	public void setParamDoseEditado(ParametroDoseUnidadeVO paramDoseEditado) {	this.paramDoseEditado = paramDoseEditado;	}
	public Integer getSeqNegativa() {	return seqNegativa;	}
	public void setSeqNegativa(Integer seqNegativa) {	this.seqNegativa = seqNegativa;	}
	public Long getSeqAux() {	return seqAux;	}
	public void setSeqAux(Long seqAux) {	this.seqAux = seqAux;	}
	public List<ProtocoloItensMedicamentoVO> getListaMedicamentosExclusao() {	return listaMedicamentosExclusao;	}
	public void setListaMedicamentosExclusao(List<ProtocoloItensMedicamentoVO> listaMedicamentosExclusao) {	this.listaMedicamentosExclusao = listaMedicamentosExclusao;	}
	public String getLabelDoseUnitaria() {	return labelDoseUnitaria;	}
	public void setLabelDoseUnitaria(String labelDoseUnitaria) {	this.labelDoseUnitaria = labelDoseUnitaria;	}
	public ParametroDoseUnidadeVO getParametroDoseSelecionado() {	return parametroDoseSelecionado;	}
	public void setParametroDoseSelecionado(ParametroDoseUnidadeVO parametroDoseSelecionado) {	this.parametroDoseSelecionado = parametroDoseSelecionado;	}
	public Boolean getDesabilitarCampoUnidade() {	return desabilitarCampoUnidade;	}
	public void setDesabilitarCampoUnidade(Boolean desabilitarCampoUnidade) {	this.desabilitarCampoUnidade = desabilitarCampoUnidade;	}
	public List<VMpmDosagem> getListaComboUnidadeParamDose() {	return listaComboUnidadeParamDose;	}
	public void setListaComboUnidadeParamDose(List<VMpmDosagem> listaComboUnidadeParamDose) {	this.listaComboUnidadeParamDose = listaComboUnidadeParamDose;	}
	public ListaAfaDescMedicamentoTipoUsoMedicamentoVO getMedicamentoSBAux() {	return medicamentoSBAux;	}
	public void setMedicamentoSBAux(ListaAfaDescMedicamentoTipoUsoMedicamentoVO medicamentoSBAux) {	this.medicamentoSBAux = medicamentoSBAux;	}
	public List<ParametroDoseUnidadeVO> getListaParamAux() {return listaParamAux; 	}
	public void setListaParamAux(List<ParametroDoseUnidadeVO> listaParamAux) {this.listaParamAux = listaParamAux;	}
}