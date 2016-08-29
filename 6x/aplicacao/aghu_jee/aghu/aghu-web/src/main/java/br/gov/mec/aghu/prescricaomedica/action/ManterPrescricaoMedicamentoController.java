package br.gov.mec.aghu.prescricaomedica.action;

import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.collections.comparators.ReverseComparator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.commons.WebUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioPadronizado;
import br.gov.mec.aghu.dominio.DominioQuimioterapico;
import br.gov.mec.aghu.dominio.DominioTipoCalculoDose;
import br.gov.mec.aghu.dominio.DominioTipoMedicaoPeso;
import br.gov.mec.aghu.dominio.DominioUnidadeBaseParametroCalculo;
import br.gov.mec.aghu.dominio.DominioUnidadeHorasMinutos;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.farmacia.vo.DiluentesVO;
import br.gov.mec.aghu.internacao.vo.MedicamentoVO;
import br.gov.mec.aghu.model.AfaFormaDosagem;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaTipoVelocAdministracoes;
import br.gov.mec.aghu.model.AfaViaAdministracao;
import br.gov.mec.aghu.model.AfaViaAdministracaoMedicamento;
import br.gov.mec.aghu.model.AfaViaAdministracaoMedicamentoId;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MpmItemPrescricaoMdto;
import br.gov.mec.aghu.model.MpmParamCalculoPrescricao;
import br.gov.mec.aghu.model.MpmParamCalculoPrescricaoId;
import br.gov.mec.aghu.model.MpmPrescricaoMdto;
import br.gov.mec.aghu.model.MpmPrescricaoMdtoId;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmPrescricaoMedicaId;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.model.MpmUnidadeMedidaMedica;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.DadosPesoAlturaVO;
import br.gov.mec.aghu.prescricaomedica.vo.ItemPrescricaoMedicaVO;
import br.gov.mec.aghu.prescricaomedica.vo.PrescricaoMedicaVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.view.VAfaDescrMdto;
import br.gov.mec.aghu.view.VMpmDosagem;

@SuppressWarnings({"PMD.AghuTooManyMethods",  "PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength", "PMD.NcssMethodCount"})
public class ManterPrescricaoMedicamentoController extends ActionController {

	private static final String PAGINA_MANTER_PRESCRICAO_MEDICA = "prescricaomedica-manterPrescricaoMedica";
	private static final Log LOG = LogFactory.getLog(ManterPrescricaoMedicamentoController.class);

	private static final long serialVersionUID = 6652292865210338276L;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	@EJB
	private IFarmaciaFacade farmaciaFacade;

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	private AghUnidadesFuncionais unidadeFuncional;

	private MpmPrescricaoMedica prescricaoMedica;

	private Boolean listaMedicamentos;//Padronizados ou não padronizados.
	
	private Boolean listaMedicamentosQuimioterapico; //Quimioterapicos ou não quimioterapicos;

	private DominioQuimioterapico listaMedicamentosAux;

	List<VMpmDosagem> listaDosagens = new ArrayList<VMpmDosagem>(0);

	private MedicamentoVO medicamentoVO;//Usado na suggestionBox de medicamentos.
	private AfaMedicamento medicamento;//Usado na suggestionBox de medicamentos.

	private BigDecimal dose;

	private MpmUnidadeMedidaMedica unidade;

	private VMpmDosagem unidadeDosagem;

	private VAfaDescrMdto diluente;

	private AfaViaAdministracao via;

	private Boolean todasAsVias = false;

	private BigDecimal volumeDiluenteMl;

	private Short qtdeHorasCorrer;

	private DominioUnidadeHorasMinutos unidHorasCorrer;

	private BigDecimal gotejo;

	private AfaTipoVelocAdministracoes tipoVelocAdministracao;

	private Boolean indBombaInfusao;

	private String horaAdministracao;

	private Boolean indSeNecessario;

	private String observacao;

	private String informacoesFarmacologicas;

	private List<ItemPrescricaoMedicaVO> listaPrescricaoMdtos = new ArrayList<ItemPrescricaoMedicaVO>(0);

	private Map< MpmPrescricaoMdto, Boolean > prescricaoMdtosSelecionados = new HashMap< MpmPrescricaoMdto, Boolean >();

	private PrescricaoMedicaVO prescricaoMedicaVO;

	private Short frequencia;
	
	private DiluentesVO diluenteSelecionado;

	private MpmTipoFrequenciaAprazamento tipoAprazamento;

	private String mensagemExibicaoModal;

	private String complemento;
	
	private Boolean exigeObservacao;

	private Boolean edicao = false;

	private Long seq = null;

	private Date dataInicioTratamento;

	private BigDecimal dosePediatrica;
	
	private MpmUnidadeMedidaMedica unidadeDosagemPediatrica;
	
	private DominioUnidadeBaseParametroCalculo unidadeBaseParametroCalculo;
	
	private DominioTipoCalculoDose tipoCalculoDose;
	
	private String mensagemModal;

	private MpmParamCalculoPrescricao parametroCalculo;
	
	private BigDecimal peso;
	
	private BigDecimal altura;

	private BigDecimal sc;

	private BigDecimal doseCalculada;
	
	private DominioTipoMedicaoPeso tipoMedicao;
	
	private int idConversacaoAnterior;
	
	private Boolean exibirCalculoDosePediatrica;
	
	private Boolean possuiPesoAlturaDia;

	
	private Boolean dosePediatricaCalculada;
	
	private boolean possuiAlteracaoCampos;	
	
	private Long lastClickedSeq;
	
	//Gap #34801
	private Boolean prescricaoAmbulatorial;
	
	private DadosPesoAlturaVO dadosPesoAlturaVO;
	
	private enum LabelMensagemModalCode{
		MSG_MODAL_CONFIRMACAO_VIA, MSG_MODAL_PESO_ALTURA_MEDICAMENTO_OU_SOLUCAO;
	}

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}

	/**
	 * Limpa os campos da tela.
	 */
	public void limpar() {
		this.edicao = false;
		this.seq = null;
		this.listaDosagens = null;
		this.listaMedicamentos = true;
		this.listaMedicamentosQuimioterapico = false;
		this.listaMedicamentosAux = DominioQuimioterapico.P;
		this.medicamentoVO = null;
		this.medicamento = null;
		this.dose = null;
		this.dataInicioTratamento = null;
		this.unidade = null;
		this.unidadeDosagem = null;
		this.diluente = null;
		this.via = null;
		this.todasAsVias = false;
		this.volumeDiluenteMl = null;
		this.qtdeHorasCorrer = null;
		this.unidHorasCorrer = null;
		this.gotejo = null;
		this.tipoVelocAdministracao = null;
		this.indBombaInfusao = false;
		this.horaAdministracao = null;
		this.indSeNecessario = false;
		this.observacao = null;
		this.informacoesFarmacologicas = null;
		this.frequencia = null;
		this.tipoAprazamento = null;
		this.mensagemExibicaoModal = "";
		this.complemento = null;
		this.listaMedicamentosAux = DominioQuimioterapico.P;
		this.exibirCalculoDosePediatrica = false;
		this.dosePediatrica = null;
		this.unidadeDosagemPediatrica = null;
		this.unidadeBaseParametroCalculo = null;
		this.tipoCalculoDose = null;
		this.doseCalculada = null;
		this.dosePediatricaCalculada = false;
		this.lastClickedSeq = null;
		this.possuiAlteracaoCampos = false;		
		this.limparDiluenteSelecionado();
	}
	
	private void limparDiluenteSelecionado(){
		this.diluenteSelecionado = null;
	}

	public void calculoDosePediatrica() {
		this.possuiAlteracaoCampos = false;	
		if(this.prescricaoMedicaVO.getIndPacPediatrico()) {
			exibirCalculoDosePediatrica = !exibirCalculoDosePediatrica;
			if(!this.possuiPesoAlturaDia) {
				this.pesoAlturaPaciente();
			} 
		}
	}

	@SuppressWarnings("PMD.NPathComplexity")
	public void calculoDose() {	
		this.changeField();
		if(dosePediatrica != null && unidadeBaseParametroCalculo != null && tipoCalculoDose != null && this.possuiPesoAlturaDia && (peso != null && tipoMedicao != null)) {
			Object[] o = this.prescricaoMedicaFacade.calculoDose(frequencia, tipoAprazamento!=null?tipoAprazamento.getSeq():null, dosePediatrica, unidadeBaseParametroCalculo, tipoCalculoDose, null, null, peso!=null?peso:BigDecimal.ZERO, altura!=null?altura:BigDecimal.ZERO, sc!=null?sc:BigDecimal.ZERO);
			if(o != null) {
				this.dosePediatricaCalculada = true;
				dose = (BigDecimal)o[0];
				doseCalculada = (BigDecimal)o[0]; 
				if(unidadeDosagemPediatrica != null) {
					List<VMpmDosagem> dosagens = this.prescricaoMedicaFacade.buscarDosagensMedicamento(this.medicamento.getMatCodigo());
					for(VMpmDosagem d : dosagens) {
						if(d.getSeqUnidade().equals(unidadeDosagemPediatrica.getDescricao())) {
							unidadeDosagem =  d;
							break;
						}
					}
				} else {
					unidadeDosagem = null;	
				}
			} else {
				doseCalculada = null;
			}
		} else {
			doseCalculada = null;
		}
	}
	
	public void pesoAlturaPaciente() {
		this.openDialog("modalPesoAlturaWG");
		this.processarDadosPesoAltura();
	}
	
	public void  processarDadosPesoAltura() {
		this.prescricaoMedicaFacade.inicializarCadastroPesoAltura(this.prescricaoMedicaVO.getId().getAtdSeq(), dadosPesoAlturaVO);
		
		parametroCalculo = this.prescricaoMedicaFacade.obterParamCalculoPrescricoesAtivoCriadoHojePeloAtendimento(this.prescricaoMedicaVO.getId().getAtdSeq());
		if(parametroCalculo != null) {
			if(parametroCalculo.getAipPesoPaciente() != null) {
				peso = parametroCalculo.getAipPesoPaciente().getPeso();
				tipoMedicao = parametroCalculo.getAipPesoPaciente().getRealEstimado();
			}
			if(parametroCalculo.getAipAlturaPaciente() != null) {
				altura = parametroCalculo.getAipAlturaPaciente().getAltura();
			}
			sc = parametroCalculo.getSc();
		} else {
			parametroCalculo = new MpmParamCalculoPrescricao();
			parametroCalculo.setId(new MpmParamCalculoPrescricaoId(null, new Date()));
		}
		
		mensagemModal = WebUtil.initLocalizedMessage(LabelMensagemModalCode.MSG_MODAL_PESO_ALTURA_MEDICAMENTO_OU_SOLUCAO.toString(),null);
	}
	
	public void calcularSC() {
		this.possuiAlteracaoCampos = false;
		DadosPesoAlturaVO vo = dadosPesoAlturaVO;//this.prescricaoMedicaFacade.obterDadosPesoAlturaVO(); FIXME MIGRAÇÃO
		if(CoreUtil.modificados(peso, vo.getPeso()) || CoreUtil.modificados(altura, vo.getAltura())) {
			sc = this.prescricaoMedicaFacade.calculaSC(prescricaoMedicaVO.getIndPacPediatrico(), peso, altura);
		}
	}
	
	public void persistirDadosPesoAltura() {
		try {
			peso = BigDecimal.ZERO.equals(peso) ? null : peso;
			altura = BigDecimal.ZERO.equals(altura) ? null : altura;
			this.prescricaoMedicaFacade.atualizarDadosPesoAltura(prescricaoMedicaVO.getPrescricaoMedica().getAtendimento().getPaciente().getCodigo(), prescricaoMedicaVO.getId().getAtdSeq(), peso, tipoMedicao, altura, null, sc, sc, dadosPesoAlturaVO);
			parametroCalculo = this.prescricaoMedicaFacade.obterParamCalculoPrescricoesAtivoCriadoHojePeloAtendimento(this.prescricaoMedicaVO.getId().getAtdSeq());
			this.possuiPesoAlturaDia = true;
			this.closeDialog("modalPesoAlturaWG");
			this.calculoDose();
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void inicio() {
		this.edicao = false;
		this.exigeObservacao = false;
		this.exibirCalculoDosePediatrica = false;
		this.dosePediatricaCalculada = false;
		this.lastClickedSeq = null;
		this.possuiAlteracaoCampos = false;
		this.possuiPesoAlturaDia = this.prescricaoMedicaFacade.possuiDadosPesoAlturaDia(this.prescricaoMedicaVO.getId().getAtdSeq());
		this.dadosPesoAlturaVO = new DadosPesoAlturaVO();

		MpmPrescricaoMedicaId idPrescricaoMedica = new MpmPrescricaoMedicaId();
		idPrescricaoMedica.setAtdSeq(this.prescricaoMedicaVO.getId()
				.getAtdSeq());
		idPrescricaoMedica.setSeq(this.prescricaoMedicaVO.getId().getSeq());
		this.prescricaoMedica = this.prescricaoMedicaFacade.obterPrescricaoMedicaPorId(idPrescricaoMedica);

		AghAtendimentos atd = this.aghuFacade.obterAghAtendimentoPorChavePrimaria(this.prescricaoMedicaVO.getId().getAtdSeq());
		
		if(atd != null){
			this.unidadeFuncional = atd.getUnidadeFuncional();
			prescricaoMedicaVO.setIndPacPediatrico(atd.getIndPacPediatrico());
			if (atd.getInternacao() != null) {
				prescricaoMedicaVO.setDtPrevAlta(atd.getInternacao()
						.getDtPrevAlta());
			}
		}

		parametroCalculo = this.prescricaoMedicaFacade.obterParamCalculoPrescricoesAtivoCriadoHojePeloAtendimento(this.prescricaoMedicaVO.getId().getAtdSeq());
		if(parametroCalculo != null) {
			if(parametroCalculo.getAipPesoPaciente() != null) {
				peso = parametroCalculo.getAipPesoPaciente().getPeso();
				tipoMedicao = parametroCalculo.getAipPesoPaciente().getRealEstimado();
			}
			if(parametroCalculo.getAipAlturaPaciente() != null) {
				altura = parametroCalculo.getAipAlturaPaciente().getAltura();
			}
			sc = parametroCalculo.getSc();
		}

		if(getPrescricaoMedicaVO() != null) {
			this.loadListaMedicamentos();
		} else {
			listaPrescricaoMdtos = new ArrayList<ItemPrescricaoMedicaVO>(0);
			prescricaoMdtosSelecionados = new HashMap< MpmPrescricaoMdto, Boolean >();
		}

		this.listaMedicamentosAux = DominioQuimioterapico.P;

		if (this.seq != null) {
			MpmPrescricaoMdto prescricaoMedicamento = prescricaoMedicaFacade.obterPrescricaoMedicamento(this.prescricaoMedica.getId().getAtdSeq(), seq);
			if(prescricaoMedicamento != null){
				this.editar(seq);
			}else{
				//controle caso o item tenha sido excluído por outro usuário
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
			}
		}
		
		AghAtendimentos atdAmbulatorial= aghuFacade.obterAtendimento(this.prescricaoMedica.getId().getAtdSeq(),null, DominioOrigemAtendimento.getOrigensDePrescricaoAmbulatorial());
		if(atdAmbulatorial != null){
			prescricaoAmbulatorial = Boolean.TRUE;
		}
	
	}
	
	private void recuperarDiluente() {
		if (diluenteSelecionado != null) {
			diluente = this.farmaciaFacade.obtemListaDiluentes(diluenteSelecionado.getSeqDiluente()).get(0);
		}else{
			diluente = null;
		}
	}

	public List<MedicamentoVO> obterMedicamentosVO(String strPesquisa){

		if(DominioQuimioterapico.P.equals(this.listaMedicamentosAux)){
			this.listaMedicamentos = true;
			this.listaMedicamentosQuimioterapico = false;
		}else if(DominioQuimioterapico.N.equals(this.listaMedicamentosAux)){
			this.listaMedicamentos = false;
			this.listaMedicamentosQuimioterapico = false;
		}else{
			this.listaMedicamentosQuimioterapico = true;
		}

		return this.returnSGWithCount(this.farmaciaFacade.obterMedicamentosVO(strPesquisa, this.listaMedicamentos, null, prescricaoAmbulatorial, this.listaMedicamentosQuimioterapico),obterMedicamentosVOCount(strPesquisa));
	}

	public Long obterMedicamentosVOCount(String strPesquisa){
		if(DominioQuimioterapico.P.equals(this.listaMedicamentosAux)){
			this.listaMedicamentos = true;
			this.listaMedicamentosQuimioterapico = false;
		}else if(DominioQuimioterapico.N.equals(this.listaMedicamentosAux)){
			this.listaMedicamentos = false;
			this.listaMedicamentosQuimioterapico = false;
		}else{
			this.listaMedicamentosQuimioterapico = true;
		}
		return this.farmaciaFacade.obterMedicamentosVOCount(strPesquisa, this.listaMedicamentos, null, prescricaoAmbulatorial, this.listaMedicamentosQuimioterapico);
	}

	public void realizarVerificacoesMedicamento(){
		if (this.medicamentoVO != null) {
			this.medicamentoVO.setMedicamento(farmaciaFacade.obterMedicamento(this.getMedicamentoVO().getMatCodigo()));
			//this.exigeObservacao = this.medicamentoVO.getIndExigeObservacao();
			
			StringBuilder returnValue = new StringBuilder("");
			if (medicamentoVO.getDescricaoMat()!= null) {
				returnValue.append(medicamentoVO.getDescricaoMedicamento());
				returnValue.append(' ');
			}
			if(medicamentoVO.getConcentracao() != null){
				Locale locBR = new Locale("pt", "BR");//Brasil 
				DecimalFormatSymbols dfSymbols = new DecimalFormatSymbols(locBR);
				dfSymbols.setDecimalSeparator(',');
				DecimalFormat format;
				if(this.medicamentoVO.getConcentracao() != null && this.medicamentoVO.getConcentracao().stripTrailingZeros().scale() <= 0) {
					format = new DecimalFormat("#,###,###,###,##0.###############", dfSymbols);
					returnValue.append(format.format(this.medicamentoVO.getConcentracao()));
					returnValue.append(' ');
				} else if (this.medicamentoVO.getConcentracao() != null) {
					format = new DecimalFormat("#,###,###,###,##0.0##############", dfSymbols);
					returnValue.append(format.format(this.medicamentoVO.getConcentracao()));
					returnValue.append(' ');
				}
			}
			
			if(medicamentoVO.getDescricaoUnidadeMedica() != null){
				returnValue.append(this.medicamentoVO.getDescricaoUnidadeMedica());
			}
			medicamentoVO.setDescricaoEditada(returnValue.toString());

			List<Integer> medMatCodigoList = new ArrayList<Integer>(); 
			medMatCodigoList.add(this.medicamentoVO.getMatCodigo());

			this.medicamento = this.farmaciaFacade.obterMedicamento(this.medicamentoVO.getMatCodigo());
			if (!this.medicamento.getIndPermiteDoseFracionada()
					&& this.dose != null && !(this.dose.stripTrailingZeros().scale() <= 0)) {
				apresentarMsgNegocio(Severity.ERROR,
						ManterPrescricaoMedicamentoExceptionCode.MPM_01128.toString());
				this.dose = null;
			}
			this.informacoesFarmacologicas = this.farmaciaFacade.obterInfromacoesFarmacologicas(medicamento);
			this.verificarExisteMensagemCadastrada();

			listaDosagens = this.prescricaoMedicaFacade.buscarDosagensMedicamento(this.medicamento.getMatCodigo());
			//VERIFICA UNIDADE MEDIDA PADRÃO PARA O MEDICAMENTO
			if(!listaDosagens.isEmpty()){
				if(listaDosagens.size() == 1){
					this.unidadeDosagem = listaDosagens.get(0);//SE só existe uma dosagem possivel para o medicamento, esta deve vir selecionada
				}else{
					AfaFormaDosagem	formaDosagem = this.farmaciaFacade.buscarDosagenPadraoMedicamento(this.medicamento.getMatCodigo());
					if(formaDosagem != null){
						for(int i = 0; i < listaDosagens.size(); i++){
							if(formaDosagem.getSeq().equals(listaDosagens.get(i).getFormaDosagem().getSeq())){
								this.unidadeDosagem = listaDosagens.get(i);
							}
						}
					}
				}
			}
			
			buscarUsualPrescrica(medicamentoVO);

			Long countVias = this.listarViasMedicamentoCount(null);
			this.todasAsVias = countVias == null || countVias == 0L;
		} else {
			this.todasAsVias = false;
		}

		this.verificarExibicaoLinkProtocoloUtilizacao();
	}
	
	private void buscarUsualPrescrica(MedicamentoVO medicamentoVO) {
		DiluentesVO diluenteVO = farmaciaFacade.buscarUsualPrescricaoPorMedicamento(medicamentoVO.getMatCodigo());
		
		if (diluenteVO.getSeqDiluente() != null) {
			this.diluenteSelecionado = diluenteVO;
		} else {
			this.diluenteSelecionado = null;
		}
	}

	public List<MedicamentoVO> obterMedicamentosEnfermeiroObstetra(String strPesquisa){
		if(DominioQuimioterapico.P.equals(this.listaMedicamentosAux)){
			this.listaMedicamentos = true;
		}else{
			this.listaMedicamentos = false;
		}
		return this.farmaciaFacade.obterMedicamentosEnfermeiroObstetra(strPesquisa, this.listaMedicamentos, null, prescricaoAmbulatorial);
	}

	public void verificarExibicaoLinkProtocoloUtilizacao(){
		if(this.medicamento != null && this.medicamento.getLinkProtocoloUtilizacao() != null && !StringUtils.isBlank(this.medicamento.getLinkProtocoloUtilizacao())){
			this.openDialog("modalProtocoloWG");
		}
	}
	
	public boolean permiteDoseFracionada() {
		return this.medicamento == null || this.medicamento.getIndPermiteDoseFracionada();
	}

	private void verificarExisteMensagemCadastrada(){
		try{
			if(DominioPadronizado.N.getCodigo() == this.listaMedicamentosAux.getCodigo()){
				AghParametros p = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_MSG_MED_NAO_CADASTRADO);

				String str = StringUtils.replace(StringUtils.replace(StringUtils.replace(p.getVlrTexto(), "#3",
						(this.medicamentoVO.getDescricaoUnidadeMedica()!=null)?this.medicamentoVO.getDescricaoUnidadeMedica():""), "#2", 
						(this.medicamentoVO.getConcentracaoFormatada() != null)?this.medicamentoVO.getConcentracaoFormatada():"0"), "#1", 
						this.medicamentoVO.getDescricaoMat());

				this.apresentarMsgNegocio(Severity.INFO, str);
			}

		}catch (BaseException e) {
			//Caso o parâmetro não seja encontrado, a princípio, para essa situação em específico, nada deve acontecer.
			LOG.error(e.getMessage(), e);
		}
	}

	public void limparCamposRelacionados(){
		this.medicamento = null;
		this.via = null;
		this.unidade = null;
		this.unidadeDosagem = null;
		this.dose = null;
		this.informacoesFarmacologicas = null;
		this.todasAsVias = false;
		this.exigeObservacao = false;
		this.dataInicioTratamento = null;
		this.diluenteSelecionado = null;
	}

	public List<VMpmDosagem> buscarDosagensMedicamento(){
		if(this.medicamento != null) {
			return this.prescricaoMedicaFacade.buscarDosagensMedicamento(this.medicamento.getMatCodigo());
		} else {
			return null;
		}
	}

	public List<MpmTipoFrequenciaAprazamento> buscarTiposFrequenciaAprazamento(String strPesquisa) {
		return this.returnSGWithCount(this.prescricaoMedicaFacade.buscarTipoFrequenciaAprazamento(strPesquisa),
										this.prescricaoMedicaFacade.buscarTipoFrequenciaAprazamentoCount(strPesquisa));
	}

	public String getDescricaoTipoFrequenciaAprazamento() {
		return buscaDescricaoTipoFrequenciaAprazamento(this.tipoAprazamento);
	}

	public String buscaDescricaoTipoFrequenciaAprazamento(
			MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento) {
		return tipoFrequenciaAprazamento != null ? tipoFrequenciaAprazamento
				.getDescricaoSintaxeFormatada(this.frequencia)
				: "";
	}

	public List<AfaViaAdministracao> listarViasMedicamento(String strPesquisa) {
		List<AfaViaAdministracao> lista = new ArrayList<AfaViaAdministracao>(0);//Tem que instanciar, se retornar null da erro na suggestion

		if(this.todasAsVias){
			return this.returnSGWithCount(this.farmaciaFacade.listarTodasAsVias(strPesquisa, this.unidadeFuncional.getSeq()),
											this.farmaciaFacade.listarTodasAsViasCount(strPesquisa, this.unidadeFuncional.getSeq()));
		}else{
			if(this.medicamento != null){
				List<Integer> medMatCodigoList = new ArrayList<Integer>(); 
				medMatCodigoList.add(this.medicamento.getMatCodigo());
				return this.returnSGWithCount(this.farmaciaFacade.listarViasMedicamento(strPesquisa, medMatCodigoList, this.unidadeFuncional.getSeq()),
												this.farmaciaFacade.listarViasMedicamentoCount(strPesquisa, medMatCodigoList, this.unidadeFuncional.getSeq()));
			}				
		}
		return lista;
	}

	public Long listarViasMedicamentoCount(String strPesquisa) {
		Long count = 0L;

		if(this.todasAsVias){
			count = this.farmaciaFacade.listarTodasAsViasCount(strPesquisa, this.unidadeFuncional.getSeq());
		}else{
			if(this.medicamento != null){
				List<Integer> listaDeIds = new ArrayList<Integer>();
				listaDeIds.add(this.medicamento.getMatCodigo());
				count =	this.farmaciaFacade.listarViasMedicamentoCount(strPesquisa, listaDeIds, this.unidadeFuncional.getSeq());
			}
		}
		return count;
	}


	public void verificarViaAssociadaAoMedicamento(){
		
		Boolean viaAssociada = true;	
		if(this.todasAsVias && this.medicamentoVO != null){
			viaAssociada = this.farmaciaFacade.verificarViaAssociadaAoMedicamento(this.medicamentoVO.getMatCodigo(), this.via.getSigla());
			if(!viaAssociada){//Se nao esta associada.
				final String msg = WebUtil.initLocalizedMessage(LabelMensagemModalCode.MSG_MODAL_CONFIRMACAO_VIA.toString(),null);
				this.mensagemExibicaoModal = MessageFormat.format(msg, this.medicamentoVO.getDescricaoEditada());
				this.openDialog("modalConfirmacaoWG");
			}
		}

		if(this.medicamentoVO != null && this.via != null && this.medicamento != null) {
			if(prescricaoMedicaFacade.validaBombaInfusao(this.unidadeFuncional, this.via, this.medicamento)) {
				this.indBombaInfusao = this.farmaciaFacade.verificarBombaInfusaoDefaultViaAssociadaAoMedicamento(this.medicamentoVO.getMatCodigo(), this.via.getSigla());
			} else  {
				this.indBombaInfusao = false;
			}
		}
	}

	public void limparAtributosViaAssociadaAoMedicamento() {
		this.possuiAlteracaoCampos = true;
		this.indBombaInfusao = false;
	}
	
	public void validarUnidadeDose(){
		if(unidadeDosagem == null){
			apresentarMsgNegocio(Severity.ERROR,"CAMPO_OBRIGATORIO", "Unidade");
		}
	}

	public void removerPrescricaoMedicamentosSelecionadas(){

		try{
			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e) {
				LOG.error("Exceção caputada:", e);
			}
			
			int nroPrescricoesMedicamentoRemovidas = 0;
			for (ItemPrescricaoMedicaVO prescMedVo: listaPrescricaoMdtos){
				MpmPrescricaoMdto prescMed = prescMedVo.getPrescricaoMedicamento();
				if (prescricaoMdtosSelecionados.get(prescMed) == true){
					MpmPrescricaoMdto prescMedOriginal = 
							this.prescricaoMedicaFacade.obterPrescricaoMedicamento(prescMed.getId().getAtdSeq(), prescMed.getId().getSeq());
					this.prescricaoMedicaFacade.desatachar(prescMedOriginal);
					
					this.prescricaoMedicaFacade.removerPrescricaoMedicamento(this.prescricaoMedica, prescMed, nomeMicrocomputador,prescMedOriginal);
					nroPrescricoesMedicamentoRemovidas++;
				}
			}
			if (nroPrescricoesMedicamentoRemovidas > 0){
				if (nroPrescricoesMedicamentoRemovidas > 1){
					apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_REMOCAO_PRESCRICAO_MEDICAMENTOS");
				} else{
					apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_REMOCAO_PRESCRICAO_MEDICAMENTO");
				}
			} else{
				apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_NENHUMA_PRESCRICAO_MEDICAMENTO_SELECIONADA_REMOCAO");
			}
			//Limpa a tela
			this.limpar();
			this.loadListaMedicamentos();
			
		}catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}


	private void loadListaMedicamentos() {

		//listaPrescricaoMdtos = prescricaoMedicaFacade.obterListaMedicamentosPrescritosPelaChavePrescricao(prescricaoMedicaVO.getId(), prescricaoMedicaVO.getDthrFim(), false);
		listaPrescricaoMdtos = prescricaoMedicaFacade.buscaListaMedicamentoPorPrescricaoMedica(prescricaoMedicaVO.getId());
		for (ItemPrescricaoMedicaVO prescMedVo : listaPrescricaoMdtos){
			MpmPrescricaoMdto prescricaoMdto = prescMedVo.getPrescricaoMedicamento();
			prescricaoMdtosSelecionados.put(prescricaoMdto, false);
		}
		
		final ComparatorChain chainSorter = new ComparatorChain();
		final BeanComparator antimicrobianoSorter = new BeanComparator("prescricaoMedicamento.indAntiMicrobiano", new ReverseComparator(new NullComparator(false)));
		final BeanComparator descricaoSorter = new BeanComparator("prescricaoMedicamento.descricaoFormatada", new NullComparator(false));
		chainSorter.addComparator(antimicrobianoSorter);
		chainSorter.addComparator(descricaoSorter);
		if(listaPrescricaoMdtos != null && !listaPrescricaoMdtos.isEmpty()) {
			Collections.sort(listaPrescricaoMdtos, chainSorter);
		}
	}

	public Boolean existeAlteracaoPendente() {
		return ((this.via != null ||
				this.frequencia != null ||
				this.diluente != null ||
				this.volumeDiluenteMl != null ||
				this.qtdeHorasCorrer != null ||
				this.unidHorasCorrer != null ||
				this.gotejo != null ||
				this.tipoVelocAdministracao != null ||
				this.indBombaInfusao !=null ||
				StringUtils.isNotBlank(this.observacao) ||
				this.horaAdministracao != null ||
				this.indSeNecessario !=null ||
				this.tipoAprazamento != null ||
				this.dosePediatrica != null ||
				this.unidadeBaseParametroCalculo != null ||
				this.tipoCalculoDose != null ||
				this.doseCalculada != null ||
				this.unidadeDosagemPediatrica != null ||
				StringUtils.isNotBlank(this.complemento) ||
				this.dose  != null||
				this.dataInicioTratamento  != null ||
				this.unidadeDosagem  != null ||
				this.informacoesFarmacologicas  != null));
					
	}
	
	public void preparaEditarLastClickedSeq() {
		this.preparaEditar(true, this.lastClickedSeq);
	}
	
	public void preparaEditar (Boolean forceEdit, Long seq)
	{
       this.lastClickedSeq = seq;
		
		if (forceEdit) {
			this.possuiAlteracaoCampos = false;
		}
		
		if (forceEdit || !this.possuiAlteracaoCampos) {
			this.editar(seq);
			this.possuiAlteracaoCampos = false;
		} else {
			this.possuiAlteracaoCampos = true;
			this.openDialog("modalConfirmacaoPendenciaEditarWG");
		}
	}
	public void editar(Long seq) {
		this.edicao = true;
		this.seq = seq;
		MpmPrescricaoMdto prescricaoMedicamento = prescricaoMedicaFacade.obterPrescricaoMedicamento(this.prescricaoMedica.getId().getAtdSeq(), seq);

		this.via = prescricaoMedicamento.getViaAdministracao();
		this.frequencia = prescricaoMedicamento.getFrequencia();
		this.diluente = (prescricaoMedicamento.getDiluente() != null) ? this.farmaciaFacade.obtemListaDiluentes(prescricaoMedicamento.getDiluente().getMatCodigo()).get(0) : null;
		this.volumeDiluenteMl = prescricaoMedicamento.getVolumeDiluenteMl();
		this.qtdeHorasCorrer = prescricaoMedicamento.getQtdeHorasCorrer();
		this.unidHorasCorrer = prescricaoMedicamento.getUnidHorasCorrer(); 
		this.gotejo = prescricaoMedicamento.getGotejo();
		this.tipoVelocAdministracao = prescricaoMedicamento.getTipoVelocAdministracao(); 
		this.indBombaInfusao = prescricaoMedicamento.getIndBombaInfusao(); 
		this.observacao = prescricaoMedicamento.getObservacao();
		this.horaAdministracao = (prescricaoMedicamento.getHoraInicioAdministracao() != null)? (new SimpleDateFormat("HH:mm")).format(prescricaoMedicamento.getHoraInicioAdministracao()) :null;
		this.indSeNecessario = prescricaoMedicamento.getIndSeNecessario();
		this.tipoAprazamento = prescricaoMedicamento.getTipoFreqAprazamento();
		
		carregaSuggestionDiluente(prescricaoMedicamento);
		
		MpmItemPrescricaoMdto itemNaoDiluente = prescricaoMedicaFacade.obterItemMedicamentoNaoDiluente(prescricaoMedicamento.getItensPrescricaoMdtos());
		
		this.dosePediatrica = itemNaoDiluente.getQtdeParamCalculo()!= null? BigDecimal.valueOf(prescricaoMedicamento.getItensPrescricaoMdtos().get(0).getQtdeParamCalculo()):null; 
		this.unidadeBaseParametroCalculo = itemNaoDiluente.getBaseParamCalculo();
		this.tipoCalculoDose = itemNaoDiluente.getTipoCalculoDose();
		this.doseCalculada = itemNaoDiluente.getDoseCalculada()!= null? BigDecimal.valueOf(itemNaoDiluente.getDoseCalculada()):null;
		this.unidadeDosagemPediatrica = itemNaoDiluente.getUnidadeMedidaMedica();
		
		this.medicamento = itemNaoDiluente.getMedicamento();

		this.medicamentoVO = new MedicamentoVO();
		this.medicamentoVO.setMatCodigo(medicamento.getMatCodigo());
		this.medicamentoVO.setDescricaoEditada(medicamento.getDescricaoEditada());
		this.medicamentoVO.setMedicamento(medicamento);
		this.medicamentoVO.setIndAntimicrobiano(itemNaoDiluente.getMedicamento().getIndAntiMicrobiano());

		this.complemento = prescricaoMedicamento.getItensPrescricaoMdtos().get(0).getObservacao();
		this.dose = prescricaoMedicamento.getItensPrescricaoMdtos().get(0).getDose();

		this.dataInicioTratamento = prescricaoMedicamento.getDthrInicioTratamento();

		this.unidadeDosagem = prescricaoMedicaFacade.obterVMpmDosagem(this.medicamento.getMatCodigo(), itemNaoDiluente.getFormaDosagem().getSeq());

		this.informacoesFarmacologicas = this.farmaciaFacade.obterInfromacoesFarmacologicas(this.medicamento);
        
		/*this.todasAsVias = prescricaoMedicamento*/
		this.listaMedicamentosAux = DominioQuimioterapico.P;
	}

	public void carregaSuggestionDiluente(MpmPrescricaoMdto prescricaoMedicamento) {
		if(this.diluente != null){
			if(this.diluente.getDescricaoMedicamento() != null && prescricaoMedicamento.getDiluente().getMatCodigo() != null){
				this.diluenteSelecionado = new DiluentesVO();
				this.diluenteSelecionado.setDescricao(this.diluente.getDescricaoMedicamento());
				this.diluenteSelecionado.setSeqDiluente(prescricaoMedicamento.getDiluente().getMatCodigo());
			}
		}
	}
	
	Enum [] fetchArgsLeftJoin = {MpmPrescricaoMdto.Fields.PRESCRICAOMEDICA,
				MpmPrescricaoMdto.Fields.VIA_ADMINISTRACAO, MpmPrescricaoMdto.Fields.DILUENTE, 
				MpmPrescricaoMdto.Fields.ITENS_PRESCRICAO_MDTOS, MpmPrescricaoMdto.Fields.TIPO_FREQ_APZ,MpmPrescricaoMdto.Fields.TIPO_VELC_ADM,
				MpmPrescricaoMdto.Fields.PRESCRICAO_MDTO_ORIGEM, MpmPrescricaoMdto.Fields.PRESCRICAO_MEDICA_ORIGEM};
	
	
	public void gravar() {
		recuperarDiluente();
		try {
			this.executarGravar();
		} catch (BaseRuntimeException e) {
			apresentarExcecaoNegocio(e);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public String gravarEVoltar() {
		try {
			this.executarGravar();
			return this.cancelar();
		} catch (BaseRuntimeException e) {
			apresentarExcecaoNegocio(e);
			return null;
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}
	
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity", "PMD.CyclomaticComplexity", "PMD.NcssMethodCount"})
	private void executarGravar() throws BaseRuntimeException, BaseException{
		RapServidores servidorLogado = registroColaboradorFacade
				.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(),
						new Date());
			
		if (this.tipoAprazamento == null) {
			apresentarMsgNegocio(Severity.ERROR,
					"CAMPO_OBRIGATORIO", "Tipo de Aprazamento");
		} else if (this.verificaRequiredFrequencia() && this.frequencia == null) {
			apresentarMsgNegocio(Severity.ERROR,
					"CAMPO_OBRIGATORIO", "Frequência");
		} else if ((this.dosePediatricaCalculada) && (this.doseCalculada==null || this.doseCalculada.doubleValue() <= 0)){
			apresentarMsgNegocio(Severity.ERROR, 
					ManterPrescricaoMedicamentoExceptionCode.MPM_IME_CK5.toString(), "Dose Calculada");
		} else if((this.dosePediatricaCalculada) && (!CoreUtil.igual(unidadeDosagemPediatrica, this.unidadeDosagem.getFormaDosagem().getUnidadeMedidaMedicas()))) {
			apresentarMsgNegocio(Severity.ERROR,
					"UNIDADE_CALCULO_DOSE_PED_DV_SER_IGUAL_UNIDADE_MED");
		} else if(this.volumeDiluenteMl == null && this.diluenteSelecionado != null){
			apresentarMsgNegocio(Severity.ERROR,
					"Informe o volume do diluente");
		}else {
			this.validaDados();
			
			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e) {
				LOG.error("Exceção caputada:", e);
			}

			MpmPrescricaoMdtoId id = new MpmPrescricaoMdtoId();
			id.setAtdSeq(this.prescricaoMedicaVO.getId().getAtdSeq());

			MpmPrescricaoMdto prescricaoMedicamento = new MpmPrescricaoMdto();
			prescricaoMedicamento.setId(id);

			if(this.edicao) {
				MpmPrescricaoMdto prescricaoMedicamentoOriginal = 
						this.prescricaoMedicaFacade.obterPrescricaoMedicamento(this.prescricaoMedica.getId().getAtdSeq(), seq);
				this.prescricaoMedicaFacade.desatachar(prescricaoMedicamentoOriginal);
				
				//prescricaoMedicamento = prescricaoMedicaFacade.obterPrescricaoMedicamento(this.prescricaoMedica.getId().getAtdSeq(), seq);
				
				prescricaoMedicamento = prescricaoMedicaFacade.
						obterMpmPrescricaoMdtoPorChavePrimaria(
								new MpmPrescricaoMdtoId(this.prescricaoMedica.getId().getAtdSeq(), seq),
								null, fetchArgsLeftJoin);
				
				//Se IndPendente 'N'
				if(DominioIndPendenteItemPrescricao.N.equals(prescricaoMedicamento.getIndPendente())) {
					//Gera nova prescrição medicamento
					MpmPrescricaoMdto novaPrescricaoMedicamento = prescricaoMedicaFacade.ajustaIndPendenteN(prescricaoMedicamento);
					novaPrescricaoMedicamento.setViaAdministracao(this.via);
					novaPrescricaoMedicamento.setFrequencia(this.frequencia);
					novaPrescricaoMedicamento.setDiluente(this.diluente != null ? this.diluente.getMedicamento() : null);
					novaPrescricaoMedicamento.setVolumeDiluenteMl(this.volumeDiluenteMl);
					novaPrescricaoMedicamento.setQtdeHorasCorrer(this.qtdeHorasCorrer);
					novaPrescricaoMedicamento.setUnidHorasCorrer(this.unidHorasCorrer);
					novaPrescricaoMedicamento.setGotejo(this.gotejo);
					novaPrescricaoMedicamento.setTipoVelocAdministracao(this.tipoVelocAdministracao);
					novaPrescricaoMedicamento.setIndBombaInfusao(this.indBombaInfusao);
					novaPrescricaoMedicamento.setObservacao(this.observacao);
					novaPrescricaoMedicamento.setIndPendente(DominioIndPendenteItemPrescricao.P);
					novaPrescricaoMedicamento.setHoraInicioAdministracao(populaDataHora(this.horaAdministracao));
					novaPrescricaoMedicamento.setIndSolucao(false);
					novaPrescricaoMedicamento.setIndSeNecessario(this.indSeNecessario);
					novaPrescricaoMedicamento.setTipoFreqAprazamento(this.tipoAprazamento);
					novaPrescricaoMedicamento.setDthrInicioTratamento(this.getDataInicioTratamento());
					
					MpmItemPrescricaoMdto itemPrescricaoMedicamento = new MpmItemPrescricaoMdto();
					itemPrescricaoMedicamento.setPrescricaoMedicamento(novaPrescricaoMedicamento);

					itemPrescricaoMedicamento.setMedicamento(this.medicamento);
					itemPrescricaoMedicamento.setObservacao(this.complemento);
					itemPrescricaoMedicamento.setDose(this.dose);
					itemPrescricaoMedicamento.setMdtoAguardaEntrega(false);
					itemPrescricaoMedicamento.setOrigemJustificativa(false);
					if(this.dosePediatricaCalculada) {
						itemPrescricaoMedicamento.setDoseCalculada(this.doseCalculada!=null?this.doseCalculada.doubleValue():null);
						itemPrescricaoMedicamento.setTipoCalculoDose(this.tipoCalculoDose);
						itemPrescricaoMedicamento.setQtdeParamCalculo(this.dosePediatrica!=null?this.dosePediatrica.doubleValue():null);
						itemPrescricaoMedicamento.setBaseParamCalculo(this.unidadeBaseParametroCalculo);
						itemPrescricaoMedicamento.setParamCalculoPrescricao(this.parametroCalculo);
						itemPrescricaoMedicamento.setUnidadeMedidaMedica(this.unidadeDosagemPediatrica);
					}

					itemPrescricaoMedicamento.setFormaDosagem(this.unidadeDosagem.getFormaDosagem());

					itemPrescricaoMedicamento
						.setQtdeCalcSist24h(this.prescricaoMedicaFacade
							.buscaCalculoQuantidade24Horas(
									this.frequencia,
									novaPrescricaoMedicamento
									.getTipoFreqAprazamento() != null ? novaPrescricaoMedicamento
											.getTipoFreqAprazamento().getSeq()
											: null,
											this.dose,
											this.unidadeDosagem.getFormaDosagem() != null ? this.unidadeDosagem
													.getFormaDosagem().getSeq()
													: null,
													this.medicamento != null ? this.medicamento
															.getMatCodigo() : null));

					List<MpmItemPrescricaoMdto> itensPrescricaoMedicamentos = new ArrayList<MpmItemPrescricaoMdto>();
					itensPrescricaoMedicamentos.add(itemPrescricaoMedicamento);

					novaPrescricaoMedicamento.setItensPrescricaoMdtos(itensPrescricaoMedicamentos);

					//remove #18867
					/*----Ajuste para editar somente o medicamento, nÃ£o o diluente. Defeito causado pela melhoria #18867----------
						//Obtem o nÃ£o diluente antigo por causa do ID
						MpmItemPrescricaoMdto itemNaoDiluenteAntigo = prescricaoMedicaFacade
								.obterItemMedicamentoNaoDiluente(
										prescricaoMedicamento
												.getItensPrescricaoMdtos());
						
						//ObtÃ©m tambÃ©m o nÃ£o diluente novo (que serÃ¡ alterado)
						MpmItemPrescricaoMdto itemNaoDiluenteNovo = prescricaoMedicaFacade
								.obterItemMedicamentoNaoDiluente(
										novaPrescricaoMedicamento
												.getItensPrescricaoMdtos());
			
						itemNaoDiluenteNovo.setMedicamento(this.medicamento);
						itemNaoDiluenteNovo.setObservacao(this.complemento);
						itemNaoDiluenteNovo.setDose(this.dose);
						if(this.dosePediatricaCalculada) {
							itemNaoDiluenteNovo.setDoseCalculada(this.doseCalculada!=null?this.doseCalculada.doubleValue():null);
							itemNaoDiluenteNovo.setTipoCalculoDose(this.tipoCalculoDose);
							itemNaoDiluenteNovo.setQtdeParamCalculo(this.dosePediatrica!=null?this.dosePediatrica.doubleValue():null);
							itemNaoDiluenteNovo.setBaseParamCalculo(this.unidadeBaseParametroCalculo);
							itemNaoDiluenteNovo.setParamCalculoPrescricao(this.parametroCalculo);
							itemNaoDiluenteNovo.setUnidadeMedidaMedica(this.unidadeDosagemPediatrica);
						}
					
						itemNaoDiluenteNovo.setFormaDosagem(this.unidadeDosagem
								.getFormaDosagem());

						itemNaoDiluenteNovo
						.setQtdeCalcSist24h(this.prescricaoMedicaFacade
								.buscaCalculoQuantidade24Horas(
										this.frequencia,
										novaPrescricaoMedicamento
										.getTipoFreqAprazamento() != null ? novaPrescricaoMedicamento
												.getTipoFreqAprazamento().getSeq()
												: null,
												this.dose,
												this.unidadeDosagem.getFormaDosagem() != null ? this.unidadeDosagem
														.getFormaDosagem().getSeq()
														: null,
														this.medicamento != null ? this.medicamento
																.getMatCodigo() : null));

						// Comentado devido ao bug registrado em http://qos-aghu.mec.gov.br/mantis/view.php?id=356
						//							if (this.unidadeDosagem != null && this.unidadeDosagem.getFdsUmmSeq() != null) {
						//								itemPrescricaoMedicamento
						//										.setUnidadeMedidaMedica(this.prescricaoMedicaFacade
						//												.obterUnidadeMedicaPorId(this.unidadeDosagem
						//														.getFdsUmmSeq()));
						//							} else {
						itemNaoDiluenteNovo.setUnidadeMedidaMedica(null);
						//							}
	
						//AJUSTE DILUENTE
						if(novaPrescricaoMedicamento.getDiluente() != null) {
							if(novaPrescricaoMedicamento.getItensPrescricaoMdtos().size() > 1) {
								----Ajuste para editar somente o medicamento, nÃ£o o diluente. Defeito causado pela melhoria #18867----------
								if (itemNaoDiluenteAntigo.getId().equals(prescricaoMedicamento.getItensPrescricaoMdtos().get(0).getId())){
									novaPrescricaoMedicamento.getItensPrescricaoMdtos().remove(1);
								}
								else{
									novaPrescricaoMedicamento.getItensPrescricaoMdtos().remove(0);									
								}
								------------------------------------------------------------------------------------------------------------
							}
	
							List<VMpmDosagem> dosagens = this.prescricaoMedicaFacade.buscarDosagensMedicamento(novaPrescricaoMedicamento.getDiluente().getMatCodigo());
							AfaFormaDosagem	formaDosagem = this.farmaciaFacade.buscarDosagenPadraoMedicamento(novaPrescricaoMedicamento.getDiluente().getMatCodigo());
							VMpmDosagem unidDosg = null;
							if(formaDosagem != null){
								for(int i = 0; i < dosagens.size(); i++){
									if(formaDosagem.getSeq().equals(dosagens.get(i).getFormaDosagem().getSeq())){
										unidDosg = dosagens.get(i);
									}
								}
							}
							else {
								if(dosagens != null && !dosagens.isEmpty()) {
									unidDosg = dosagens.get(0);
									formaDosagem = dosagens.get(0).getFormaDosagem();
								}
							}

							MpmItemPrescricaoMdto itemPrescricaoMedicamento = new MpmItemPrescricaoMdto(); 
							itemPrescricaoMedicamento.setMedicamento(novaPrescricaoMedicamento.getDiluente());
							itemPrescricaoMedicamento.setDose((formaDosagem != null && formaDosagem.getFatorConversaoUp()!=null)?formaDosagem.getFatorConversaoUp():novaPrescricaoMedicamento.getDiluente().getConcentracao());
							itemPrescricaoMedicamento.setFormaDosagem(unidDosg!=null?unidDosg.getFormaDosagem():null);
							itemPrescricaoMedicamento.setMdtoAguardaEntrega(false);
							itemPrescricaoMedicamento.setOrigemJustificativa(false);
							itemPrescricaoMedicamento
							.setQtdeCalcSist24h(this.prescricaoMedicaFacade
									.buscaCalculoQuantidade24Horas(
											this.frequencia,
											prescricaoMedicamento
											.getTipoFreqAprazamento() != null ? prescricaoMedicamento
													.getTipoFreqAprazamento().getSeq()
													: null,
													this.dose,
													this.unidadeDosagem.getFormaDosagem() != null ? this.unidadeDosagem
															.getFormaDosagem().getSeq()
															: null,
															novaPrescricaoMedicamento.getDiluente() != null ? novaPrescricaoMedicamento.getDiluente()
																	.getMatCodigo() : null));
							itemPrescricaoMedicamento.setPrescricaoMedicamento(novaPrescricaoMedicamento);
							novaPrescricaoMedicamento.getItensPrescricaoMdtos().add(itemPrescricaoMedicamento);
						/*}
						else {
							if(novaPrescricaoMedicamento.getItensPrescricaoMdtos().size() > 1) {
								----Ajuste para remover o diluente. Defeito causado pela melhoria #18867----------
								if (itemNaoDiluenteAntigo.getId().equals(prescricaoMedicamento.getItensPrescricaoMdtos().get(0).getId())){
									novaPrescricaoMedicamento.getItensPrescricaoMdtos().remove(1);
								}
								else{
									novaPrescricaoMedicamento.getItensPrescricaoMdtos().remove(0);									
								}
								------------------------------------------------------------------------------------------------------------
							}
						}
*/
					this.prescricaoMedicaFacade.persistirPrescricaoMedicamento(novaPrescricaoMedicamento, nomeMicrocomputador, null);

					//Atualiza a atual
					prescricaoMedicamento.setIndPendente(DominioIndPendenteItemPrescricao.A);
					prescricaoMedicamento.setDthrFim(this.prescricaoMedicaFacade.isPrescricaoVigente(this.prescricaoMedica.getDthrInicio(),
							this.prescricaoMedica.getDthrFim()) ? this.prescricaoMedica.getDthrMovimento() : this.prescricaoMedica.getDthrInicio());
					prescricaoMedicamento.setServidorMovimentado(servidorLogado);

					this.prescricaoMedicaFacade.persistirPrescricaoMedicamento(prescricaoMedicamento, nomeMicrocomputador, prescricaoMedicamentoOriginal);

					verificaGrupoMedicamentoTuberculostatico(prescricaoMedicamento);

				} else {
					//remove #18867
					//Obtendo o item não diluente antes de setar o novo diluente
					MpmItemPrescricaoMdto itemNaoDiluente = prescricaoMedicaFacade.obterItemMedicamentoNaoDiluente(prescricaoMedicamento.getItensPrescricaoMdtos());
				
					prescricaoMedicamento.setServidor(servidorLogado);
					prescricaoMedicamento.setViaAdministracao(this.via);
					prescricaoMedicamento.setFrequencia(this.frequencia);
					prescricaoMedicamento.setDiluente(this.diluente != null ? this.diluente.getMedicamento() : null);
					prescricaoMedicamento.setVolumeDiluenteMl(this.volumeDiluenteMl);
					prescricaoMedicamento.setQtdeHorasCorrer(this.qtdeHorasCorrer);
					prescricaoMedicamento.setUnidHorasCorrer(this.unidHorasCorrer);
					prescricaoMedicamento.setGotejo(this.gotejo);
					prescricaoMedicamento.setTipoVelocAdministracao(this.tipoVelocAdministracao);
					prescricaoMedicamento.setIndBombaInfusao(this.indBombaInfusao);
					prescricaoMedicamento.setObservacao(this.observacao);
					prescricaoMedicamento.setHoraInicioAdministracao(populaDataHora(this.horaAdministracao));
					prescricaoMedicamento.setIndSeNecessario(this.indSeNecessario);
					prescricaoMedicamento.setTipoFreqAprazamento(this.tipoAprazamento);
					prescricaoMedicamento.setDthrInicioTratamento(this.getDataInicioTratamento());

					for(MpmItemPrescricaoMdto itemPrescricaoMedicamento : prescricaoMedicamento.getItensPrescricaoMdtos()) {
						//remove #18867
						//----Ajuste para editar somente o medicamento, não o diluente. Defeito causado pela melhoria #18867----------
						if (itemNaoDiluente.getId().equals(itemPrescricaoMedicamento.getId())){
						//------------------------------------------------------------------------------------------------------------*/
							itemPrescricaoMedicamento.setMedicamento(this.medicamento);
							itemPrescricaoMedicamento.setObservacao(this.complemento);
							itemPrescricaoMedicamento.setDose(this.dose);
							if(this.dosePediatricaCalculada) {
								itemPrescricaoMedicamento.setDoseCalculada(this.doseCalculada!=null?this.doseCalculada.doubleValue():null);
								itemPrescricaoMedicamento.setTipoCalculoDose(this.tipoCalculoDose);
								itemPrescricaoMedicamento.setQtdeParamCalculo(this.dosePediatrica!=null?this.dosePediatrica.doubleValue():null);
								itemPrescricaoMedicamento.setBaseParamCalculo(this.unidadeBaseParametroCalculo);
								itemPrescricaoMedicamento.setParamCalculoPrescricao(this.parametroCalculo);
								itemPrescricaoMedicamento.setUnidadeMedidaMedica(this.unidadeDosagemPediatrica);
							}

							itemPrescricaoMedicamento.setFormaDosagem(this.unidadeDosagem
									.getFormaDosagem());
	
							itemPrescricaoMedicamento
								.setQtdeCalcSist24h(this.prescricaoMedicaFacade.buscaCalculoQuantidade24Horas(this.frequencia,
											prescricaoMedicamento
											.getTipoFreqAprazamento() != null ? prescricaoMedicamento
													.getTipoFreqAprazamento().getSeq()
													: null,
													this.dose,
													this.unidadeDosagem.getFormaDosagem() != null ? this.unidadeDosagem
															.getFormaDosagem().getSeq()
															: null,
															this.medicamento != null ? this.medicamento
																	.getMatCodigo() : null));
	
							// Comentado devido ao bug registrado em http://qos-aghu.mec.gov.br/mantis/view.php?id=356
							//							if (this.unidadeDosagem != null && this.unidadeDosagem.getFdsUmmSeq() != null) {
							//								itemPrescricaoMedicamento
							//										.setUnidadeMedidaMedica(this.prescricaoMedicaFacade
							//												.obterUnidadeMedicaPorId(this.unidadeDosagem
							//														.getFdsUmmSeq()));
							//							} else {
							itemPrescricaoMedicamento.setUnidadeMedidaMedica(null);
							//							}
						//remove #18867
							}
					}
					
					//remove #18867
					//AJUSTE DILUENTE
					if(prescricaoMedicamento.getDiluente() != null) {
//						if(prescricaoMedicamento.getItensPrescricaoMdtos().size() > 1) {
//							//----Ajuste para editar AQUI somente o diluente, nÃ£o o medicamento. Defeito causado pela melhoria #18867----------
//							if (itemNaoDiluente.getId().equals(prescricaoMedicamento.getItensPrescricaoMdtos().get(0).getId())){
//								prescricaoMedicamento.getItensPrescricaoMdtos().remove(1);
//							}
//							else{
//								prescricaoMedicamento.getItensPrescricaoMdtos().remove(0);									
//							}
//							//------------------------------------------------------------------------------------------------------------
//						}

						List<VMpmDosagem> dosagens = this.prescricaoMedicaFacade.buscarDosagensMedicamento(prescricaoMedicamento.getDiluente().getMatCodigo());
						AfaFormaDosagem	formaDosagem = this.farmaciaFacade.buscarDosagenPadraoMedicamento(prescricaoMedicamento.getDiluente().getMatCodigo());
						VMpmDosagem unidDosg = null;
						if(formaDosagem != null){
							for(int i = 0; i < dosagens.size(); i++){
								if(formaDosagem.getSeq().equals(dosagens.get(i).getFormaDosagem().getSeq())){
									unidDosg = dosagens.get(i);
								}
							}
						}
						else {
							if(dosagens != null && !dosagens.isEmpty()) {
								unidDosg = dosagens.get(0);
								formaDosagem = dosagens.get(0).getFormaDosagem();
							}
						}
						
						MpmItemPrescricaoMdto itemPrescricaoMedicamento = new MpmItemPrescricaoMdto(); 
						itemPrescricaoMedicamento.setMedicamento(medicamentoVO.getMedicamento());
						itemPrescricaoMedicamento.setDose((formaDosagem != null && formaDosagem.getFatorConversaoUp()!=null)?formaDosagem.getFatorConversaoUp():prescricaoMedicamento.getDiluente().getConcentracao());
						itemPrescricaoMedicamento.setFormaDosagem(unidDosg!=null?unidDosg.getFormaDosagem():null);
						itemPrescricaoMedicamento.setPrescricaoMedicamento(prescricaoMedicamento);
						itemPrescricaoMedicamento.setMdtoAguardaEntrega(false);
						itemPrescricaoMedicamento.setOrigemJustificativa(false);
						itemPrescricaoMedicamento
						.setQtdeCalcSist24h(this.prescricaoMedicaFacade
								.buscaCalculoQuantidade24Horas(
										this.frequencia,
										prescricaoMedicamento
										.getTipoFreqAprazamento() != null ? prescricaoMedicamento
												.getTipoFreqAprazamento().getSeq()
												: null,
												this.dose,
												this.unidadeDosagem.getFormaDosagem() != null ? this.unidadeDosagem
														.getFormaDosagem().getSeq()
														: null,
														prescricaoMedicamento.getDiluente() != null ? prescricaoMedicamento.getDiluente()
																.getMatCodigo() : null));
					//prescricaoMedicamento.getItensPrescricaoMdtos().add(itemPrescricaoMedicamento);
					//remove #18867
					}
//					else {
//						if(prescricaoMedicamento.getItensPrescricaoMdtos().size() > 1) {
//							if (prescricaoMedicamento.getItensPrescricaoMdtos().get(0).getId().equals(itemNaoDiluente.getId())){
//								prescricaoMedicamento.getItensPrescricaoMdtos().remove(1);
//							}
//							else{
//								prescricaoMedicamento.getItensPrescricaoMdtos().remove(0);									
//							}
//						}	
//					}

					this.prescricaoMedicaFacade.persistirPrescricaoMedicamento(prescricaoMedicamento, nomeMicrocomputador, prescricaoMedicamentoOriginal);

					verificaGrupoMedicamentoTuberculostatico(prescricaoMedicamento);

					
				}
			} else {
				prescricaoMedicamento.setPrescricaoMedica(this.prescricaoMedica);
				prescricaoMedicamento.setViaAdministracao(this.via);
				prescricaoMedicamento.setFrequencia(this.frequencia);
				prescricaoMedicamento.setDiluente(this.diluente != null ? this.diluente.getMedicamento() : null);
				prescricaoMedicamento.setVolumeDiluenteMl(this.volumeDiluenteMl);
				prescricaoMedicamento.setQtdeHorasCorrer(this.qtdeHorasCorrer);
				prescricaoMedicamento.setUnidHorasCorrer(this.unidHorasCorrer);
				prescricaoMedicamento.setGotejo(this.gotejo);
				prescricaoMedicamento.setTipoVelocAdministracao(this.tipoVelocAdministracao);
				prescricaoMedicamento.setIndBombaInfusao(this.indBombaInfusao);
				prescricaoMedicamento.setObservacao(this.observacao);
				prescricaoMedicamento.setIndPendente(DominioIndPendenteItemPrescricao.P);
				prescricaoMedicamento.setHoraInicioAdministracao(populaDataHora(this.horaAdministracao));
				prescricaoMedicamento.setIndSolucao(false);
				prescricaoMedicamento.setIndSeNecessario(this.indSeNecessario);
				prescricaoMedicamento.setIndItemRecomendadoAlta(false);
				prescricaoMedicamento.setDthrInicio(getDataHoraInicio());
				prescricaoMedicamento.setDthrFim(this.prescricaoMedica.getDthrFim());
				prescricaoMedicamento.setTipoFreqAprazamento(this.tipoAprazamento);
				prescricaoMedicamento.setDthrInicioTratamento(this.getDataInicioTratamento());

				MpmItemPrescricaoMdto itemPrescricaoMedicamento = new MpmItemPrescricaoMdto();
				itemPrescricaoMedicamento.setPrescricaoMedicamento(prescricaoMedicamento);

				itemPrescricaoMedicamento.setMedicamento(this.medicamento);
				itemPrescricaoMedicamento.setObservacao(this.complemento);
				itemPrescricaoMedicamento.setDose(this.dose);
				itemPrescricaoMedicamento.setMdtoAguardaEntrega(false);
				itemPrescricaoMedicamento.setOrigemJustificativa(false);
				if(this.dosePediatricaCalculada) {
					itemPrescricaoMedicamento.setDoseCalculada(this.doseCalculada!=null?this.doseCalculada.doubleValue():null);
					itemPrescricaoMedicamento.setTipoCalculoDose(this.tipoCalculoDose);
					itemPrescricaoMedicamento.setQtdeParamCalculo(this.dosePediatrica!=null?this.dosePediatrica.doubleValue():null);
					itemPrescricaoMedicamento.setBaseParamCalculo(this.unidadeBaseParametroCalculo);
					itemPrescricaoMedicamento.setParamCalculoPrescricao(this.parametroCalculo);
					itemPrescricaoMedicamento.setUnidadeMedidaMedica(this.unidadeDosagemPediatrica);
				}

				itemPrescricaoMedicamento.setFormaDosagem(this.unidadeDosagem.getFormaDosagem());

				itemPrescricaoMedicamento
					.setQtdeCalcSist24h(this.prescricaoMedicaFacade
						.buscaCalculoQuantidade24Horas(
								this.frequencia,
								prescricaoMedicamento
								.getTipoFreqAprazamento() != null ? prescricaoMedicamento
										.getTipoFreqAprazamento().getSeq()
										: null,
										this.dose,
										this.unidadeDosagem.getFormaDosagem() != null ? this.unidadeDosagem
												.getFormaDosagem().getSeq()
												: null,
												this.medicamento != null ? this.medicamento
														.getMatCodigo() : null));

				// Comentado devido ao bug registrado em http://qos-aghu.mec.gov.br/mantis/view.php?id=356
				//					if (this.unidadeDosagem != null && this.unidadeDosagem.getFdsUmmSeq() != null) {
				//						itemPrescricaoMedicamento
				//								.setUnidadeMedidaMedica(this.prescricaoMedicaFacade
				//										.obterUnidadeMedicaPorId(this.unidadeDosagem
				//												.getFdsUmmSeq()));
				//					}

				List<MpmItemPrescricaoMdto> itensPrescricaoMedicamentos = new ArrayList<MpmItemPrescricaoMdto>();
				itensPrescricaoMedicamentos.add(itemPrescricaoMedicamento);

				prescricaoMedicamento.setItensPrescricaoMdtos(itensPrescricaoMedicamentos);

				//AJUSTE DILUENTE
				//remove #18867
				/*if(prescricaoMedicamento.getDiluente() != null) {
					List<VMpmDosagem> dosagens = this.prescricaoMedicaFacade.buscarDosagensMedicamento(prescricaoMedicamento.getDiluente().getMatCodigo());
					AfaFormaDosagem	formaDosagem = this.farmaciaFacade.buscarDosagenPadraoMedicamento(prescricaoMedicamento.getDiluente().getMatCodigo());
					VMpmDosagem unidDosg = null;
					if(formaDosagem != null){
						for(int i = 0; i < dosagens.size(); i++){
							if(formaDosagem.getSeq().equals(dosagens.get(i).getFormaDosagem().getSeq())){
								unidDosg = dosagens.get(i);
							}
						}
					}
					else {
						if(dosagens != null && !dosagens.isEmpty()) {
							unidDosg = dosagens.get(0);
							formaDosagem = dosagens.get(0).getFormaDosagem();
						}
					}
					
					MpmItemPrescricaoMdto diluenteItemPrescricaoMedicamento = new MpmItemPrescricaoMdto(); 
					diluenteItemPrescricaoMedicamento.setMedicamento(prescricaoMedicamento.getDiluente());
					diluenteItemPrescricaoMedicamento.setDose((formaDosagem != null && formaDosagem.getFatorConversaoUp()!=null)?formaDosagem.getFatorConversaoUp():prescricaoMedicamento.getDiluente().getConcentracao());
					diluenteItemPrescricaoMedicamento.setFormaDosagem(unidDosg != null ? unidDosg.getFormaDosagem():null);
					diluenteItemPrescricaoMedicamento.setPrescricaoMedicamento(prescricaoMedicamento);
					diluenteItemPrescricaoMedicamento.setMdtoAguardaEntrega(false);
					diluenteItemPrescricaoMedicamento.setOrigemJustificativa(false);
					diluenteItemPrescricaoMedicamento
					.setQtdeCalcSist24h(this.prescricaoMedicaFacade
							.buscaCalculoQuantidade24Horas(
									this.frequencia,
									prescricaoMedicamento
									.getTipoFreqAprazamento() != null ? prescricaoMedicamento
											.getTipoFreqAprazamento().getSeq()
											: null,
											this.dose,
											this.unidadeDosagem.getFormaDosagem() != null ? this.unidadeDosagem
													.getFormaDosagem().getSeq()
													: null,
													prescricaoMedicamento.getDiluente() != null ? prescricaoMedicamento.getDiluente()
															.getMatCodigo() : null));
					prescricaoMedicamento.getItensPrescricaoMdtos().add(diluenteItemPrescricaoMedicamento);
				}
				else {
					if(prescricaoMedicamento.getItensPrescricaoMdtos().size() > 1) {
						----Ajuste para editar somente o medicamento, nÃ£o o diluente. Defeito causado pela melhoria #18867----------
						MpmItemPrescricaoMdto itemNaoDiluente = prescricaoMedicaFacade
								.obterItemMedicamentoNaoDiluente(
										prescricaoMedicamento
												.getItensPrescricaoMdtos());
						if (itemNaoDiluente.getId().equals(prescricaoMedicamento.getItensPrescricaoMdtos().get(0).getId())){
							prescricaoMedicamento.getItensPrescricaoMdtos().remove(1);
						}
						else{
							prescricaoMedicamento.getItensPrescricaoMdtos().remove(0);									
						}
					}
				}*/
				this.prescricaoMedicaFacade.persistirPrescricaoMedicamento(prescricaoMedicamento, nomeMicrocomputador,null);

				verificaGrupoMedicamentoTuberculostatico(prescricaoMedicamento);

			}

			if(edicao) {
				apresentarMsgNegocio(Severity.INFO, ManterPrescricaoMedicamentoExceptionCode.PRESCRICAO_MEDICAMENTO_ALTERADA_SUCESSO.toString());
			} else {
				apresentarMsgNegocio(Severity.INFO, ManterPrescricaoMedicamentoExceptionCode.PRESCRICAO_MEDICAMENTO_INSERIDA_SUCESSO.toString());
			}
			this.limpar();
			this.loadListaMedicamentos();
			this.setPossuiAlteracaoCampos(false);	
		}
	}

	private void verificaGrupoMedicamentoTuberculostatico(
			MpmPrescricaoMdto prescricaoMedicamento) throws BaseException {
		try {
			this.prescricaoMedicaFacade.verificaGrupoUsoMedicamentoTuberculostatico(prescricaoMedicamento.getItensPrescricaoMdtos());
		} catch (BaseException e) {
			if(e.getCode().toString().equals("MPM_01148") || e.getCode().toString().equals("MPM_01149")){
				apresentarExcecaoNegocio(e);
			} else{
				throw new BaseException(e);
			}
		}
	}

	private Date getDataHoraInicio() throws ApplicationBusinessException {
		return (this.prescricaoMedicaFacade.isPrescricaoVigente(this.prescricaoMedica.getDthrInicio(),
					this.prescricaoMedica.getDthrFim()) ? this.prescricaoMedica.getDthrMovimento() : this.prescricaoMedica.getDthrInicio());
	}

	@SuppressWarnings("PMD.NPathComplexity")
	private void validaDados() throws BaseException {
		if(this.via == null){
			throw new ApplicationBusinessException(ManterPrescricaoMedicamentoExceptionCode.OBRIGATORIO_PREENCHER_VIA);			
		}
		
		if (this.medicamento.getIndExigeObservacao() && StringUtils.isBlank(this.complemento)) {
			throw new ApplicationBusinessException(ManterPrescricaoMedicamentoExceptionCode.OBRIGATORIO_PREENCHER_COMPLEMENTO);
		}
		
		if (this.dose != null && this.unidadeDosagem == null) {
			throw new ApplicationBusinessException(ManterPrescricaoMedicamentoExceptionCode.OBRIGATORIO_PREENCHER_UNIDADE_DOSAGEM_AO_PREENCHER_DOSE);
		}

		if (this.dose == null && this.unidadeDosagem != null) {
			throw new ApplicationBusinessException(ManterPrescricaoMedicamentoExceptionCode.OBRIGATORIO_PREENCHER_DOSE_AO_PREENCHER_UNIDADE_DOSAGEM);
		}

		if (verificaRequiredFrequencia() && this.frequencia == null) {
			throw new ApplicationBusinessException(ManterPrescricaoMedicamentoExceptionCode.OBRIGATORIO_PREENCHER_FREQUENCIA_PARA_ESTE_APRAZAMENTO);
		}

		if (this.qtdeHorasCorrer != null && this.unidHorasCorrer == null) {
			throw new ApplicationBusinessException(ManterPrescricaoMedicamentoExceptionCode.OBRIGATORIO_PREENCHER_UNIDADE_AO_PREENCHER_CORRER_EM);
		}

		if (this.qtdeHorasCorrer == null && this.unidHorasCorrer != null) {
			throw new ApplicationBusinessException(ManterPrescricaoMedicamentoExceptionCode.OBRIGATORIO_PREENCHER_CORRER_EM_AO_PREENCHER_UNIDADE);
		}

		if (this.gotejo != null && this.tipoVelocAdministracao == null) {
			throw new ApplicationBusinessException(ManterPrescricaoMedicamentoExceptionCode.OBRIGATORIO_PREENCHER_UNIDADE_INFUSAO_AO_PREENCHER_VELOCIDADE_INFUSAO);
		}

		if (this.indBombaInfusao && !prescricaoMedicaFacade.validaBombaInfusao(this.unidadeFuncional, this.via, this.medicamento)) {
			throw new ApplicationBusinessException(ManterPrescricaoMedicamentoExceptionCode.APENAS_PERMITIDO_MARCAR_BOMBA_SE_HA_PERMISSAO_UNIDADE_MEDICAMENTO_E_VIA_ADMINISTRACAO);
		}
		validarDataInicioTratamento();
	}

	private void validarDataInicioTratamento() throws BaseException {
		if (this.dataInicioTratamento != null && !this.getEdicao()) {
			// Esta data não poderá ser maior que o fim da prescrição médica.
			if (DateUtil.truncaData(this.getDataInicioTratamento()).after(DateUtil.truncaData(this.prescricaoMedica.getDthrFim()))) {
				throw new ApplicationBusinessException(ManterPrescricaoMedicamentoExceptionCode.DATA_INICIO_TRATAMENTO_MAIOR_FIM_PRESCRICAO);
			}
			//A data não pode ser menor que o dia atual, menos o valor informado no parâmetro P_DIAS_RETROATIVOS_INICIO_TRATAMENTO
			Date dataInicioTratamentoRetroativoPermitido = calcularDataInicioTratamentoRetroativoPermitido();
			if (DateUtil.truncaData(this.getDataInicioTratamento()).before(dataInicioTratamentoRetroativoPermitido)) {
				throw new ApplicationBusinessException(ManterPrescricaoMedicamentoExceptionCode.DATA_INICIO_TRATAMENTO_MENOR_PARAMETRO, getDataInicioTratamentoRetroativoPermitidoFormatada(dataInicioTratamentoRetroativoPermitido));
			}
			//A data não pode ser menor que a data de nascimento do paciente.
			if (DateUtil.truncaData(this.getDataInicioTratamento()).before(getDtNascimentoPaciente())) {
				throw new ApplicationBusinessException(ManterPrescricaoMedicamentoExceptionCode.DATA_INICIO_TRATAMENTO_MENOR_NASCIMENTO);
			}
		}
	}

	private String getDataInicioTratamentoRetroativoPermitidoFormatada(Date dataInicioTratamentoRetroativoPermitido) {
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		return dateFormat.format(dataInicioTratamentoRetroativoPermitido);
	}

	private Date getDtNascimentoPaciente() {
		Date dtNascimentoPaciente1 = getPrescricaoMedicaVO().getPrescricaoMedica().getAtendimento().getPaciente().getDtNascimento();
		return DateUtil.truncaData(dtNascimentoPaciente1);
	}

	/**
	 * Calcula a data que o sistema permite lançar inicio de tratamento retroativo.
	 * 
	 * Busca o parâmetro P_DIAS_RETROATIVOS_INICIO_TRATAMENTO e subtrai da data atual.
	 * @return Date com hora truncada.
	 * @throws ApplicationBusinessException
	 */
	private Date calcularDataInicioTratamentoRetroativoPermitido() throws ApplicationBusinessException {
		Calendar dataInicioTratamentoRetroativoPermitido = Calendar.getInstance();
		AghParametros parametro = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_DIAS_RETROATIVOS_INICIO_TRATAMENTO);
		
		if (parametro.getVlrNumerico() != null) {
			dataInicioTratamentoRetroativoPermitido.add(Calendar.DAY_OF_MONTH, - (parametro.getVlrNumerico().intValue()));
		}
		return DateUtil.truncaData(dataInicioTratamentoRetroativoPermitido.getTime());
	}

	/**
	 * Recebe uma String hora (formato HH:mm) e retorna uma data que representa
	 * esta hora.
	 * 
	 * @param hora
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private Date populaDataHora(String hora) throws ApplicationBusinessException {
		try {
			if (StringUtils.isNotBlank(hora)) {
				String[] arrayHora = hora.split(":");
				if (arrayHora.length == 2) {
					Calendar cal = Calendar.getInstance();
					cal.set(Calendar.DAY_OF_MONTH, 1);
					cal.set(Calendar.HOUR_OF_DAY, Integer.valueOf(arrayHora[0]));
					cal.set(Calendar.MINUTE, Integer.valueOf(arrayHora[1]));
					cal.set(Calendar.SECOND, 0);
					cal.set(Calendar.MILLISECOND, 0);
					return cal.getTime();
				}
			}
			return null;
		} catch (Exception e) {
			LOG.error(e.getMessage(),e);
			throw new ApplicationBusinessException(ManterPrescricaoMedicamentoExceptionCode.HORA_INVALIDA);
		}
	}

	public boolean verificaRequiredFrequencia() {
		return this.tipoAprazamento != null && this.tipoAprazamento.getIndDigitaFrequencia();
	}

	public void verificarFrequencia() {
		if (!this.verificaRequiredFrequencia()) {
			this.frequencia = null;
		}
		this.calculoDose();
	}

	public boolean medicamentoSelecionadoAntimicrobiano() {
		boolean retorno = false;
		if (getMedicamentoVO() != null && getMedicamentoVO().getIndAntimicrobiano() != null) {
			retorno = getMedicamentoVO().getIndAntimicrobiano().booleanValue();
			if (retorno && getDataInicioTratamento() == null) {
				try {
					setDataInicioTratamento(prescricaoMedicaFacade.atualizaInicioTratamento(getDataHoraInicio(), populaDataHora(this.horaAdministracao)));
				} catch (ApplicationBusinessException e) {
					this.apresentarExcecaoNegocio(e);
				}
			}
		}
		return retorno;
	}

	public void verificarDose() {
		if (this.dose != null && this.dose.doubleValue() <= 0) {
			this.apresentarMsgNegocio(Severity.ERROR,ManterPrescricaoMedicamentoExceptionCode.DOSE_PRECISA_SER_MAIOR_QUE_ZERO.toString());
		}
	}

	public List<VAfaDescrMdto> buscarDiluentes(){
		return this.farmaciaFacade.obtemListaDiluentes();
	}
	
	public List<DiluentesVO> buscarListaVinculoDiluente(String descricaoMedicamento) {
		List<DiluentesVO> listaDiluentesVO;  
	
		if (this.getMedicamentoVO() != null && this.getMedicamentoVO().getMatCodigo() != null) {
			 AfaMedicamento medicamento = farmaciaFacade.obterMedicamento(this.getMedicamentoVO().getMatCodigo());
			listaDiluentesVO = farmaciaFacade.recuperaListaVinculoDiluente((String) descricaoMedicamento, medicamento); 
		} else {
			listaDiluentesVO = farmaciaFacade.populaVoDiluentesVAfaDescrMdto(descricaoMedicamento);
		}
		
		return listaDiluentesVO;
	}

	public List<AfaTipoVelocAdministracoes> buscarTiposVelocidadeAdministracao(){
		return this.farmaciaFacade.obtemListaTiposVelocidadeAdministracao();
	}

	public Boolean isPadraoBI() {
		if(medicamento != null && via != null) {
			AfaViaAdministracaoMedicamentoId chave = new AfaViaAdministracaoMedicamentoId(medicamento.getMatCodigo(),via.getSigla());
			AfaViaAdministracaoMedicamento viaAdmMdtos = this.farmaciaFacade.buscarAfaViaAdministracaoMedimanetoPorChavePrimaria(chave);
			if(viaAdmMdtos != null) {
				return viaAdmMdtos.getDefaultBi();
			}
		}
		return false;
	}

	public String cancelar() {
		this.lastClickedSeq = null;		
		if (this.medicamentoVO != null && this.possuiAlteracaoCampos) {
			this.openDialog("modalConfirmacaoPendenciaWG");
			return null;
		}
		this.prescricaoMedicaVO = null;
		this.limpar();
		this.prescricaoMedicaVO = null;
		return this.voltar();
	}

	public String voltar() {
		this.lastClickedSeq = null; 
		this.possuiAlteracaoCampos = false;
		this.limpar();
		this.prescricaoMedicaVO = null;
		return PAGINA_MANTER_PRESCRICAO_MEDICA ;
	}

	public void limparConteudoComplemento(){
		this.complemento = null;
	}

	public void limparConteudoPesoAltura(){
		this.peso = null;
		this.altura = null;
		this.sc = null;
	}
	
	//### GETTERs and SETTERs ###
	public Boolean getListaMedicamentos() {
		return listaMedicamentos;
	}

	public void setListaMedicamentos(Boolean listaMedicamentos) {
		this.listaMedicamentos = listaMedicamentos;
	}

	public DominioQuimioterapico getListaMedicamentosAux() {
		return listaMedicamentosAux;
	}

	public void setListaMedicamentosAux(DominioQuimioterapico listaMedicamentosAux) {
		this.listaMedicamentosAux = listaMedicamentosAux;
	}

	public BigDecimal getDose() {
		return dose;
	}

	public void setDose(BigDecimal dose) {
		this.dose = dose;
	}

	public MpmUnidadeMedidaMedica getUnidade() {
		return unidade;
	}

	public void setUnidade(MpmUnidadeMedidaMedica unidade) {
		this.unidade = unidade;
	}

	public AfaViaAdministracao getVia() {
		return via;
	}

	public void setVia(AfaViaAdministracao via) {
		this.via = via;
	}

	public VMpmDosagem getUnidadeDosagem() {
		return unidadeDosagem;
	}

	public void setUnidadeDosagem(VMpmDosagem unidadeDosagem) {
		this.unidadeDosagem = unidadeDosagem;
	}

	public List<ItemPrescricaoMedicaVO> getListaPrescricaoMdtos() {
		return listaPrescricaoMdtos;
	}

	public void setListaPrescricaoMdtos(
			List<ItemPrescricaoMedicaVO> listaPrescricaoMdtos) {
		this.listaPrescricaoMdtos = listaPrescricaoMdtos;
	}

	public PrescricaoMedicaVO getPrescricaoMedicaVO() {
		return prescricaoMedicaVO;
	}

	public void setPrescricaoMedicaVO(PrescricaoMedicaVO prescricaoMedicaVO) {
		this.prescricaoMedicaVO = prescricaoMedicaVO;
	}

	public Map<MpmPrescricaoMdto, Boolean> getPrescricaoMdtosSelecionados() {
		return prescricaoMdtosSelecionados;
	}

	public void setPrescricaoMdtosSelecionados(
			Map<MpmPrescricaoMdto, Boolean> prescricaoMdtosSelecionados) {
		this.prescricaoMdtosSelecionados = prescricaoMdtosSelecionados;
	}

	public VAfaDescrMdto getDiluente() {
		return diluente;
	}

	public void setDiluente(VAfaDescrMdto diluente) {
		this.diluente = diluente;
	}

	public BigDecimal getVolumeDiluenteMl() {
		return volumeDiluenteMl;
	}

	public void setVolumeDiluenteMl(BigDecimal volumeDiluenteMl) {
		this.volumeDiluenteMl = volumeDiluenteMl;
	}

	public Short getQtdeHorasCorrer() {
		return qtdeHorasCorrer;
	}

	public void setQtdeHorasCorrer(Short qtdeHorasCorrer) {
		this.qtdeHorasCorrer = qtdeHorasCorrer;
	}

	public DominioUnidadeHorasMinutos getUnidHorasCorrer() {
		return unidHorasCorrer;
	}

	public void setUnidHorasCorrer(DominioUnidadeHorasMinutos unidHorasCorrer) {
		this.unidHorasCorrer = unidHorasCorrer;
	}

	public BigDecimal getGotejo() {
		return gotejo;
	}

	public void setGotejo(BigDecimal gotejo) {
		this.gotejo = gotejo;
	}

	public AfaTipoVelocAdministracoes getTipoVelocAdministracao() {
		return tipoVelocAdministracao;
	}

	public void setTipoVelocAdministracao(
			AfaTipoVelocAdministracoes tipoVelocAdministracao) {
		this.tipoVelocAdministracao = tipoVelocAdministracao;
	}

	public Boolean getIndBombaInfusao() {
		return indBombaInfusao;
	}

	public void setIndBombaInfusao(Boolean indBombaInfusao) {
		this.indBombaInfusao = indBombaInfusao;
	}

	public Boolean getTodasAsVias() {
		return todasAsVias;
	}

	public void setTodasAsVias(Boolean todasAsVias) {
		this.todasAsVias = todasAsVias;
	}

	public Short getFrequencia() {
		return frequencia;
	}

	public void setFrequencia(Short frequencia) {
		this.frequencia = frequencia;
	}

	public MpmTipoFrequenciaAprazamento getTipoAprazamento() {
		return tipoAprazamento;
	}

	public void setTipoAprazamento(MpmTipoFrequenciaAprazamento tipoAprazamento) {
		this.tipoAprazamento = tipoAprazamento;
	}

	public String getMensagemExibicaoModal() {
		return mensagemExibicaoModal;
	}

	public void setMensagemExibicaoModal(String mensagemExibicaoModal) {
		this.mensagemExibicaoModal = mensagemExibicaoModal;
	}


	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public String getHoraAdministracao() {
		return horaAdministracao;
	}

	public void setHoraAdministracao(String horaAdministracao) {
		this.horaAdministracao = horaAdministracao;
	}

	public Boolean getIndSeNecessario() {
		return indSeNecessario;
	}

	public void setIndSeNecessario(Boolean indSeNecessario) {
		this.indSeNecessario = indSeNecessario;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public String getInformacoesFarmacologicas() {
		return informacoesFarmacologicas;
	}

	public void setInformacoesFarmacologicas(String informacoesFarmacologicas) {
		this.informacoesFarmacologicas = informacoesFarmacologicas;
	}

	public MedicamentoVO getMedicamentoVO() {
		return medicamentoVO;
	}

	public void setMedicamentoVO(MedicamentoVO medicamentoVO) {
		this.medicamentoVO = medicamentoVO;
	}

	public AfaMedicamento getMedicamento() {
		return medicamento;
	}

	public void setMedicamento(AfaMedicamento medicamento) {
		this.medicamento = medicamento;
	}

	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	public MpmPrescricaoMedica getPrescricaoMedica() {
		return prescricaoMedica;
	}

	public void setPrescricaoMedica(MpmPrescricaoMedica prescricaoMedica) {
		this.prescricaoMedica = prescricaoMedica;
	}

	public Long getSeq() {
		return seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}

	public Boolean getEdicao() {
		return edicao;
	}

	public Date getDataInicioTratamento() {
		return dataInicioTratamento;
	}

	public void setDataInicioTratamento(Date dataInicioTratamento) {
		this.dataInicioTratamento = dataInicioTratamento;
	}

	public List<VMpmDosagem> buscarDosagens(){
		if(this.medicamento != null) {
			return this.prescricaoMedicaFacade.buscarDosagensMedicamento(this.medicamento.getMatCodigo());
		}
		return new ArrayList<VMpmDosagem>();
	}
	
	public List<MpmUnidadeMedidaMedica> buscarDosagensPed(){
		if(this.medicamento != null) {
			return this.prescricaoMedicaFacade.getListaUnidadeMedidaMedicaAtivasPeloMedFmDosagPeloCodigoOuDescricao(this.medicamento.getMatCodigo());	
		}
		return new ArrayList<MpmUnidadeMedidaMedica>();
	}

	public DominioUnidadeBaseParametroCalculo[] getListaUnidadeBaseParametroCalculo() {
		return new DominioUnidadeBaseParametroCalculo[]{DominioUnidadeBaseParametroCalculo.K, DominioUnidadeBaseParametroCalculo.M};
	}

	public DominioTipoCalculoDose[] getListaTipoCalculoDose() {
		return new DominioTipoCalculoDose[]{DominioTipoCalculoDose.U, DominioTipoCalculoDose.D};
	}

	
	
	public void setListaDosagens(List<VMpmDosagem> listaDosagens) {
		this.listaDosagens = listaDosagens;
	}

	public int getIdConversacaoAnterior() {
		return idConversacaoAnterior;
	}

	public void setIdConversacaoAnterior(int idConversacaoAnterior) {
		this.idConversacaoAnterior = idConversacaoAnterior;
	}

	public Boolean getExigeObservacao() {
		return exigeObservacao;
	}

	public void setExigeObservacao(Boolean exigeObservacao) {
		this.exigeObservacao = exigeObservacao;
	}

	public String getMensagemModal() {
		return mensagemModal;
	}

	public void setMensagemModal(String mensagemModal) {
		this.mensagemModal = mensagemModal;
	}

	public MpmParamCalculoPrescricao getParametroCalculo() {
		return parametroCalculo;
	}

	public void setParametroCalculo(MpmParamCalculoPrescricao parametroCalculo) {
		this.parametroCalculo = parametroCalculo;
	}

	public BigDecimal getPeso() {
		return peso;
	}

	public void setPeso(BigDecimal peso) {
		this.peso = peso;
	}

	public BigDecimal getAltura() {
		return altura;
	}

	public void setAltura(BigDecimal altura) {
		this.altura = altura;
	}

	public BigDecimal getSc() {
		return sc;
	}

	public void setSc(BigDecimal sc) {
		this.sc = sc;
	}

	public DominioTipoMedicaoPeso getTipoMedicao() {
		return tipoMedicao;
	}

	public void setTipoMedicao(DominioTipoMedicaoPeso tipoMedicao) {
		this.tipoMedicao = tipoMedicao;
	}

	public BigDecimal getDosePediatrica() {
		return dosePediatrica;
	}

	public void setDosePediatrica(BigDecimal dosePediatrica) {
		this.dosePediatrica = dosePediatrica;
	}

	public MpmUnidadeMedidaMedica getUnidadeDosagemPediatrica() {
		return unidadeDosagemPediatrica;
	}

	public void setUnidadeDosagemPediatrica(
			MpmUnidadeMedidaMedica unidadeDosagemPediatrica) {
		this.unidadeDosagemPediatrica = unidadeDosagemPediatrica;
	}

	public DominioUnidadeBaseParametroCalculo getUnidadeBaseParametroCalculo() {
		return unidadeBaseParametroCalculo;
	}

	public void setUnidadeBaseParametroCalculo(
			DominioUnidadeBaseParametroCalculo unidadeBaseParametroCalculo) {
		this.unidadeBaseParametroCalculo = unidadeBaseParametroCalculo;
	}

	public DominioTipoCalculoDose getTipoCalculoDose() {
		return tipoCalculoDose;
	}

	public void setTipoCalculoDose(DominioTipoCalculoDose tipoCalculoDose) {
		this.tipoCalculoDose = tipoCalculoDose;
	}

	public Boolean getExibirCalculoDosePediatrica() {
		return exibirCalculoDosePediatrica;
	}

	public void setExibirCalculoDosePediatrica(Boolean exibirCalculoDosePediatrica) {
		this.exibirCalculoDosePediatrica = exibirCalculoDosePediatrica;
	}

	public Boolean getPossuiPesoAlturaDia() {
		return possuiPesoAlturaDia;
	}

	public void setPossuiPesoAlturaDia(Boolean possuiPesoAlturaDia) {
		this.possuiPesoAlturaDia = possuiPesoAlturaDia;
	}
	
	public void changeField(){
		this.setPossuiAlteracaoCampos(true);
	}
	
	public void setPossuiAlteracaoCampos(boolean possuiAlteracaoCampos) {
		this.possuiAlteracaoCampos = possuiAlteracaoCampos;
	}

	public boolean isPossuiAlteracaoCampos() {
		return possuiAlteracaoCampos;
	}

	public Boolean getPrescricaoAmbulatorial() {
		return prescricaoAmbulatorial;
	}

	public void setPrescricaoAmbulatorial(Boolean prescricaoAmbulatorial) {
		this.prescricaoAmbulatorial = prescricaoAmbulatorial;
	}

	public Boolean getListaMedicamentosQuimioterapico() {
		return listaMedicamentosQuimioterapico;
	}

	public void setListaMedicamentosQuimioterapico(
			Boolean listaMedicamentosQuimioterapico) {
		this.listaMedicamentosQuimioterapico = listaMedicamentosQuimioterapico;
	}

	public DiluentesVO getDiluenteSelecionado() {
		return diluenteSelecionado;
	}

	public void setDiluenteSelecionado(DiluentesVO diluenteSelecionado) {
		this.diluenteSelecionado = diluenteSelecionado;
	}
	
}