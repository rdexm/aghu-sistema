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
import java.util.HashSet;
import java.util.Iterator;
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
import br.gov.mec.aghu.dominio.DominioDuracaoCalculo;
import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioPadronizado;
import br.gov.mec.aghu.dominio.DominioQuimioterapico;
import br.gov.mec.aghu.dominio.DominioTipoCalculoDose;
import br.gov.mec.aghu.dominio.DominioTipoMedicaoPeso;
import br.gov.mec.aghu.dominio.DominioUnidadeBaseParametroCalculo;
import br.gov.mec.aghu.dominio.DominioUnidadeHorasMinutos;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.internacao.vo.MedicamentoVO;
import br.gov.mec.aghu.model.AfaFormaDosagem;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaTipoVelocAdministracoes;
import br.gov.mec.aghu.model.AfaViaAdministracao;
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
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.DadosPesoAlturaVO;
import br.gov.mec.aghu.prescricaomedica.vo.PrescricaoMedicaVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.view.VMpmDosagem;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.commons.WebUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;


@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength"})
public class ManterPrescricaoSolucaoController extends ActionController {

	private static final String PAGINA_FARMACIA_MEDICAMENTO_CRUD = "farmacia-medicamentoCRUD";
	private static final String PAGINA_MANTER_PRESCRICAO_MEDICA = "prescricaomedica-manterPrescricaoMedica";
	private static final long serialVersionUID = -5159107032113993320L;
	private static final Log LOG = LogFactory.getLog(ManterPrescricaoSolucaoController.class);

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IFarmaciaFacade farmaciaFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	private PrescricaoMedicaVO prescricaoMedicaVO;

	private MpmPrescricaoMedica prescricaoMedica;

	private MpmPrescricaoMdto prescricaoMedicamento = new MpmPrescricaoMdto(); 

	private List<MpmPrescricaoMdto> listaPrescricaoSolucoes = new ArrayList<MpmPrescricaoMdto>(0);

	private Map<MpmPrescricaoMdto, Boolean> listaPrescricaoSolucoesSelecionadas = new HashMap< MpmPrescricaoMdto, Boolean >();

	private List<MpmItemPrescricaoMdto> listaMedicamentosSolucao = new ArrayList<MpmItemPrescricaoMdto>(0);

	private List<VMpmDosagem> listaDosagens = new ArrayList<VMpmDosagem>(0);

	private BigDecimal dose;

	private MpmUnidadeMedidaMedica unidade;

	private VMpmDosagem unidadeDosagem;

	private AfaViaAdministracao via;

	private Boolean todasAsVias = false;

	private AghUnidadesFuncionais unidadeFuncional;

	private Boolean listaMedicamentos;//Padronizados ou não padronizados.
	
	private Boolean listaMedicamentosQuimioterapico; //Quimioterapicos ou não quimioterapicos;

	private DominioQuimioterapico listaMedicamentosAux;

	private MedicamentoVO medicamentoVO;//Usado na suggestionBox de medicamentos.

	private AfaMedicamento medicamento;//Usado na suggestionBox de medicamentos.

	private String mensagemExibicaoModal;

	private Short frequencia;

	private MpmTipoFrequenciaAprazamento tipoAprazamento;

	private DominioUnidadeHorasMinutos unidHorasCorrer;

	private Short qtdeHorasCorrer;

	private AfaTipoVelocAdministracoes tipoVelocAdministracao;

	private BigDecimal gotejo;

	private Boolean indBombaInfusao;

	private String horaAdministracao;

	private Boolean indSeNecessario;

	private String observacao;

	private String informacoesFarmacologicas;

	private String complemento;

	private int indice = 0;

	private Integer matCodigo = null;

	private Boolean edicao = false;

	private Boolean edicaoPrescricaoMedicamento = false;

	private Long seq = null;

	private Date dataInicioTratamento;

	private Integer numMedicamentos = 0;

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
	
	private Integer duracao;
	
	private DominioDuracaoCalculo unidadeTempo;

	private AfaViaAdministracao viaDosePed;
	
	private Short frequenciaDosePed;
	
	private MpmTipoFrequenciaAprazamento tipoAprazamentoDosePed;
	
	private int idConversacaoAnterior;
	
	private Boolean exibirCalculoDosePediatrica;
	
	private Boolean possuiPesoAlturaDia;
	
	private Integer matCodigoMedicamentoEdicao;
	
	private Boolean dosePediatricaCalculada;
	
	private DadosPesoAlturaVO dadosPesoAlturaVO ;

	//Gap #34801
	private Boolean prescricaoAmbulatorial;

	private enum ManterPrescricaoSolucaoControllerExceptionCode implements BusinessExceptionCode {
		OBRIGATORIO_PREENCHER_VIA, DOSE_PRECISA_SER_MAIOR_QUE_ZERO, HORA_INVALIDA, OBRIGATORIO_PREENCHER_COMPLEMENTO_MEDICAMENTO_SOLUCAO,
		OBRIGATORIO_PREENCHER_UNIDADE_DOSAGEM_AO_PREENCHER_DOSE, OBRIGATORIO_PREENCHER_UNIDADE_DOSAGEM_AO_PREENCHER_DOSE_MEDICAMENTO_SOLUCAO,
		MPM_01128, MPM_01128_MEDICAMENTO_SOLUCAO, 
		OBRIGATORIO_PREENCHER_DOSE_AO_PREENCHER_UNIDADE_DOSAGEM, OBRIGATORIO_PREENCHER_DOSE_AO_PREENCHER_UNIDADE_DOSAGEM_MEDICAMENTO_SOLUCAO,
		OBRIGATORIO_PREENCHER_FREQUENCIA_PARA_ESTE_APRAZAMENTO, OBRIGATORIO_PREENCHER_UNIDADE_AO_PREENCHER_CORRER_EM,
		OBRIGATORIO_PREENCHER_CORRER_EM_AO_PREENCHER_UNIDADE, OBRIGATORIO_PREENCHER_UNIDADE_INFUSAO_AO_PREENCHER_VELOCIDADE_INFUSAO,
		PERIODO_PRESCRICAO_MEDICAMENTO_INVALIDO, MSG_0336_UNIDADE_NAO_PERMITE_BOMBA_DE_INFUSAO_NA_PRESCRICAO,
		PRESCRICAO_SOLUCAO_INSERIDA_SUCESSO,PRESCRICAO_SOLUCAO_ALTERADA_SUCESSO, LISTA_MEDICAMENTOS_SOLUCAO_VAZIA,
		MEDICAMENTO_SELECIONADO_CRUD, UNIDADE_CALCULO_DOSE_PED_DV_SER_IGUAL_UNIDADE_MED;
	}

	private enum LabelMensagemModalCode{
		MSG_MODAL_CONFIRMACAO_VIA, MSG_MODAL_PESO_ALTURA_MEDICAMENTO_OU_SOLUCAO;
	}

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}	
	
	public void inicio() {
		this.edicaoPrescricaoMedicamento = false;
		this.numMedicamentos = 0;
		this.exibirCalculoDosePediatrica = false;
		this.possuiPesoAlturaDia = this.prescricaoMedicaFacade.possuiDadosPesoAlturaDia(this.prescricaoMedicaVO.getId().getAtdSeq());
		this.dosePediatricaCalculada = false;
		this.dadosPesoAlturaVO = new DadosPesoAlturaVO();
		
		MpmPrescricaoMedicaId idPrescricaoMedica = new MpmPrescricaoMedicaId();
		idPrescricaoMedica.setAtdSeq(this.prescricaoMedicaVO.getId()
				.getAtdSeq());
		idPrescricaoMedica.setSeq(this.prescricaoMedicaVO.getId().getSeq());
		this.prescricaoMedica = this.prescricaoMedicaFacade
		.obterPrescricaoMedicaPorId(idPrescricaoMedica);

		AghAtendimentos atd = this.aghuFacade.obterAghAtendimentoPorChavePrimaria(this.prescricaoMedicaVO.getId().getAtdSeq());
		if(atd != null){
			this.unidadeFuncional = atd.getUnidadeFuncional();
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
			listaPrescricaoSolucoes = prescricaoMedicaFacade.obterListaMedicamentosPrescritosPelaChavePrescricao(prescricaoMedicaVO.getId(), prescricaoMedicaVO.getDthrFim(), true);
			for (MpmPrescricaoMdto prescricaoMdto : listaPrescricaoSolucoes){
				listaPrescricaoSolucoesSelecionadas.put(prescricaoMdto, false);
			}

			final ComparatorChain chainSorter = new ComparatorChain();
			final BeanComparator antimicrobianoSorter = new BeanComparator("indAntiMicrobiano", new ReverseComparator(new NullComparator(false)));
			final BeanComparator descricaoSorter = new BeanComparator("descricaoFormatada", new NullComparator(false));
			chainSorter.addComparator(antimicrobianoSorter);
			chainSorter.addComparator(descricaoSorter);
			if(listaPrescricaoSolucoes != null && !listaPrescricaoSolucoes.isEmpty()) {
				Collections.sort(listaPrescricaoSolucoes, chainSorter);//TODO MIGRAÇÃO: InvocationTargetException: java.lang.reflect.InvocationTargetException
			}

		
		} else {
			listaPrescricaoSolucoes = new ArrayList<MpmPrescricaoMdto>(0);
			listaPrescricaoSolucoesSelecionadas = new HashMap< MpmPrescricaoMdto, Boolean >();
		}

		this.listaMedicamentosAux = DominioQuimioterapico.P;

		if (this.seq != null) {
			prescricaoMedicamento = prescricaoMedicaFacade.obterPrescricaoMedicamento(this.prescricaoMedica.getId().getAtdSeq(), seq);
			if(prescricaoMedicamento != null){
				this.editar(seq);
			}else{
				//controle caso o item tenha sido excluído por outro usuário
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
			}			
		}
		
		AghAtendimentos atdAmbulatorial= aghuFacade.obterAtendimento(this.prescricaoMedica.getId().getAtdSeq(),null,	DominioOrigemAtendimento.getOrigensDePrescricaoAmbulatorial());
		if(atdAmbulatorial != null){
			prescricaoAmbulatorial = Boolean.TRUE;
		}
	
	}

	public void calculoDosePediatrica() {
		//this.editarMedicamento(item);
		if(this.prescricaoMedicaVO.getIndPacPediatrico()) {
			exibirCalculoDosePediatrica = !exibirCalculoDosePediatrica;
			if(!this.possuiPesoAlturaDia) {
				this.pesoAlturaPaciente();
			}
		}
	}

	@SuppressWarnings("PMD.NPathComplexity")
	public void calculoDose() {
		if(dosePediatrica != null && unidadeBaseParametroCalculo != null && tipoCalculoDose != null && this.possuiPesoAlturaDia && (peso != null && tipoMedicao != null)) {
			Object[] o = this.prescricaoMedicaFacade.calculoDose(frequenciaDosePed, tipoAprazamentoDosePed!=null?tipoAprazamentoDosePed.getSeq():null, dosePediatrica, unidadeBaseParametroCalculo, tipoCalculoDose, duracao, unidadeTempo, peso!=null?peso:BigDecimal.ZERO, altura!=null?altura:BigDecimal.ZERO, sc!=null?sc:BigDecimal.ZERO);
			if(o != null) {
				dose = (BigDecimal)o[0];
				doseCalculada = (BigDecimal)o[0]; 
				this.dosePediatricaCalculada =  true;
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
				
				if(frequenciaDosePed != null) {
					frequencia = frequenciaDosePed;
				} else {
					frequencia = null;
				}

				if(tipoAprazamentoDosePed != null) {
					tipoAprazamento = tipoAprazamentoDosePed;
				} else {
					tipoAprazamento = null;
				}

				if(viaDosePed != null) {
					via = viaDosePed;
				} else {
					via = null;
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
		
		mensagemModal =  WebUtil.initLocalizedMessage(LabelMensagemModalCode.MSG_MODAL_PESO_ALTURA_MEDICAMENTO_OU_SOLUCAO.toString(),null);
	}
	
	public void calcularSC() {
		DadosPesoAlturaVO vo = dadosPesoAlturaVO;//this.prescricaoMedicaFacade.obterDadosPesoAlturaVO();
		if(CoreUtil.modificados(peso, vo.getPeso()) || CoreUtil.modificados(altura, vo.getAltura())) {
			sc = this.prescricaoMedicaFacade.calculaSC(prescricaoMedicaVO.getIndPacPediatrico(), peso, altura);
		}
	}
	
	public void persistirDadosPesoAltura() {
		try {				
			altura = BigDecimal.ZERO.equals(altura) ? null : altura;
			peso = BigDecimal.ZERO.equals(peso) ? null : peso;
			this.prescricaoMedicaFacade.atualizarDadosPesoAltura(prescricaoMedicaVO.getPrescricaoMedica().getAtendimento().getPaciente().getCodigo(), prescricaoMedicaVO.getId().getAtdSeq(), peso, tipoMedicao, altura, null, sc, sc, dadosPesoAlturaVO);
			parametroCalculo = this.prescricaoMedicaFacade.obterParamCalculoPrescricoesAtivoCriadoHojePeloAtendimento(this.prescricaoMedicaVO.getId().getAtdSeq());
			this.possuiPesoAlturaDia = true;
			this.closeDialog("modalPesoAlturaWG");
			this.calculoDose();
		}
		catch(BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void selecionarTipoMedicamento(){
		if(DominioQuimioterapico.P.equals(this.listaMedicamentosAux)){
			this.listaMedicamentos = true;
			this.listaMedicamentosQuimioterapico = false;
		}else if(DominioQuimioterapico.N.equals(this.listaMedicamentosAux)){
			this.listaMedicamentos = false;
			this.listaMedicamentosQuimioterapico = false;
		}else{
			this.listaMedicamentosQuimioterapico = true;
		}
	}

	public List<MedicamentoVO> obterMedicamentos(String strPesquisa){
		if (this.listaMedicamentos == null){
			this.selecionarTipoMedicamento();
		}	
		
		return this.returnSGWithCount(this.farmaciaFacade.obterMedicamentosVO(strPesquisa, this.listaMedicamentos, null, prescricaoAmbulatorial, this.listaMedicamentosQuimioterapico),obterMedicamentosCount(strPesquisa));
		//List<AfaMedicamento> lista = this.prescricaoMedicaFacade.obterMedicamentos(strPesquisa, this.listaMedicamentos); 
		//return lista;
	}
	
	public String editarMedicamento(Integer matcodigo){
		this.matCodigoMedicamentoEdicao = matcodigo;		
		return PAGINA_FARMACIA_MEDICAMENTO_CRUD;
	}

	public Long obterMedicamentosCount(String strPesquisa){
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
		//return this.prescricaoMedicaFacade.obterMedicamentosCount(strPesquisa, this.listaMedicamentos);
	}
	
	public List<MedicamentoVO> obterMedicamentosEnfermeiroObstetra(Object strPesquisa) {

		if (DominioPadronizado.S.equals(this.listaMedicamentosAux)) {
			this.listaMedicamentos = true;
		} else {
			this.listaMedicamentos = false;
		}
		return this.farmaciaFacade.obterMedicamentosEnfermeiroObstetra((String) strPesquisa, this.listaMedicamentos, null, prescricaoAmbulatorial);
	}

	protected void setListaMedicamentosAuxDominioPadronizadoSim() {
		this.setListaMedicamentosAux(DominioQuimioterapico.P);
	}

	public boolean permiteDoseFracionada() {
		return this.medicamento == null
		|| this.medicamento.getIndPermiteDoseFracionada();
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
		return new DominioTipoCalculoDose[]{DominioTipoCalculoDose.U,DominioTipoCalculoDose.D, DominioTipoCalculoDose.M, DominioTipoCalculoDose.H};
	}

	public DominioDuracaoCalculo[] getListaDuracaoCalculo() {
		return new DominioDuracaoCalculo[]{DominioDuracaoCalculo.H, DominioDuracaoCalculo.M};
	}

	public List<AfaViaAdministracao> listarViasMedicamento(String strPesquisa) {
		List<AfaViaAdministracao> lista = new ArrayList<AfaViaAdministracao>(0);//Tem que instanciar, se retornar null da erro na suggestion

		if(this.todasAsVias){
			lista = this.farmaciaFacade.listarTodasAsVias((String)strPesquisa, this.unidadeFuncional.getSeq());
		}else{
			if(this.listaMedicamentosSolucao != null && !this.listaMedicamentosSolucao.isEmpty()){
				List<Integer> listaDeIds = new ArrayList<Integer>();
				for (MpmItemPrescricaoMdto itemMedicamento : this.listaMedicamentosSolucao) {
					listaDeIds.add(itemMedicamento.getMedicamento().getMatCodigo());
				}
				lista = this.farmaciaFacade.listarViasMedicamento((String)strPesquisa, listaDeIds, this.unidadeFuncional.getSeq());
				if(lista != null && lista.size() > 0) {
					lista = new ArrayList<AfaViaAdministracao>(new HashSet<AfaViaAdministracao>(lista));
				}
			}
		}
		return this.returnSGWithCount(lista,listarViasMedicamentoCount(strPesquisa));
	}

	public Long listarViasMedicamentoCount(String strPesquisa) {
		Long count = 0L;

		if(this.todasAsVias){
			count = this.farmaciaFacade.listarTodasAsViasCount((String)strPesquisa, this.unidadeFuncional.getSeq());
		}else{
			if(this.listaMedicamentosSolucao != null && !this.listaMedicamentosSolucao.isEmpty()){
				List<Integer> listaDeIds = new ArrayList<Integer>();
				for (MpmItemPrescricaoMdto itemMedicamento : this.listaMedicamentosSolucao) {
					listaDeIds.add(itemMedicamento.getMedicamento().getMatCodigo());
				}
				count =	this.farmaciaFacade.listarViasMedicamentoCount((String)strPesquisa, listaDeIds, this.unidadeFuncional.getSeq());
			}
		}
		return count;
	}


	public List<AfaViaAdministracao> listarViasMedicamentoPed(String strPesquisa) {
		List<AfaViaAdministracao> lista = new ArrayList<AfaViaAdministracao>(0);//Tem que instanciar, se retornar null da erro na suggestion
		if(this.medicamentoVO != null){
			List<Integer> listaDeIds = new ArrayList<Integer>();
			listaDeIds.add(medicamentoVO.getMatCodigo());
			lista = this.farmaciaFacade.listarViasMedicamento((String)strPesquisa, listaDeIds, this.unidadeFuncional.getSeq());
			if(lista != null && lista.size() > 0) {
				lista = new ArrayList<AfaViaAdministracao>(new HashSet<AfaViaAdministracao>(lista));
			}
		}
		return this.returnSGWithCount(lista,listarViasMedicamentoPedCount(strPesquisa));
	}

	public Long listarViasMedicamentoPedCount(String strPesquisa) {
		Long count = 0L;

		if(this.medicamentoVO != null){
			List<Integer> listaDeIds = new ArrayList<Integer>();
			listaDeIds.add(medicamentoVO.getMatCodigo());
			count =	this.farmaciaFacade.listarViasMedicamentoCount((String)strPesquisa, listaDeIds, this.unidadeFuncional.getSeq());
		}
		return count;
	}

	protected void verificarViaAssociadaAoMedicamentoExibirModal() {
		Boolean viaAssociada = true;
		if(this.todasAsVias && this.listaMedicamentosSolucao != null && !this.listaMedicamentosSolucao.isEmpty()){
			for (MpmItemPrescricaoMdto item : this.listaMedicamentosSolucao) {
				viaAssociada = this.farmaciaFacade.verificarViaAssociadaAoMedicamento(item.getMedicamento().getMatCodigo(), this.via.getSigla());
				if(!viaAssociada){//Se nao esta associada.
					this.openDialog("modalConfirmacaoWG");
					final String msg = WebUtil.initLocalizedMessage(LabelMensagemModalCode.MSG_MODAL_CONFIRMACAO_VIA.toString(),null);
					this.mensagemExibicaoModal = MessageFormat.format(msg, item.getMedicamento().getDescricaoEditada());
					break;
				}
			}
		}
	}

	protected void verificarViaAssociadaAoMedicamentoBombaInfusao() {
		Boolean viaAssociada = true;
		if(this.listaMedicamentosSolucao != null && !this.listaMedicamentosSolucao.isEmpty() && this.via != null) {
			for (MpmItemPrescricaoMdto item : this.listaMedicamentosSolucao) {
				viaAssociada = this.farmaciaFacade.verificarBombaInfusaoDefaultViaAssociadaAoMedicamento(item.getMedicamento().getMatCodigo(), this.via.getSigla());
				if(viaAssociada && prescricaoMedicaFacade.validaBombaInfusao(
						this.unidadeFuncional, this.via, item.getMedicamento())) {
					this.indBombaInfusao = true;
					break;
				} else {
					this.indBombaInfusao = false;
				}
			}
		}
	}

	public void verificarViaAssociadaAoMedicamento() {
		this.verificarViaAssociadaAoMedicamentoExibirModal();
		this.verificarViaAssociadaAoMedicamentoBombaInfusao();
	}

	public void limparAtributosViaAssociadaAoMedicamento() {
		this.indBombaInfusao = false;
	}

	public boolean verificaRequiredFrequencia() {
		return this.tipoAprazamento != null
		&& this.tipoAprazamento.getIndDigitaFrequencia();
	}

	public void verificarFrequencia() {
		if (!this.verificaRequiredFrequencia()) {
			this.frequencia = null;
		}
	}

	public void verificarFrequenciaPed() {
		if (!(this.tipoAprazamentoDosePed != null && this.tipoAprazamentoDosePed.getIndDigitaFrequencia())) {
			this.frequenciaDosePed = null;
		}
		this.calculoDose();
	}

	public List<MpmTipoFrequenciaAprazamento> buscarTiposFrequenciaAprazamento(String strPesquisa) {
		return this.prescricaoMedicaFacade.buscarTipoFrequenciaAprazamento((String) strPesquisa);
	}

	public String getDescricaoTipoFrequenciaAprazamento() {
		return buscaDescricaoTipoFrequenciaAprazamento(this.tipoAprazamento);
	}

	public String getDescricaoTipoFrequenciaAprazamentoPed() {
		return buscaDescricaoTipoFrequenciaAprazamentoPed(this.tipoAprazamentoDosePed);
	}

	public String buscaDescricaoTipoFrequenciaAprazamento(MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento) {
		return tipoFrequenciaAprazamento != null ? tipoFrequenciaAprazamento
				.getDescricaoSintaxeFormatada(this.frequencia)
				: "";
	}

	public String buscaDescricaoTipoFrequenciaAprazamentoPed(
			MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamentoPed) {
		return tipoFrequenciaAprazamentoPed != null ? tipoFrequenciaAprazamentoPed
				.getDescricaoSintaxeFormatada(this.frequenciaDosePed)
				: "";
	}

	public List<AfaTipoVelocAdministracoes> buscarTiposVelocidadeAdministracao(){
		return this.farmaciaFacade.obtemListaTiposVelocidadeAdministracao();
	}

	/**
	 * Limpa os campos da tela.
	 */
	public void limpar() {
		this.numMedicamentos = 0;
		this.edicao = false;
		this.edicaoPrescricaoMedicamento = false;
		this.prescricaoMedicamento = new MpmPrescricaoMdto();
		this.seq = null;
		this.matCodigo = null;
		this.listaDosagens = null;
		this.listaMedicamentos = true;
		this.listaMedicamentosAux = DominioQuimioterapico.P;
		this.medicamento = null;
		this.medicamentoVO = null;
		this.dose = null;
		this.dataInicioTratamento = null;
		this.unidade = null;
		this.unidadeDosagem = null;
		this.via = null;
		this.todasAsVias = false;
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
		this.duracao = null;
		this.unidadeTempo = null;
		this.frequenciaDosePed = null;
		this.tipoAprazamentoDosePed = null;
		this.viaDosePed = null;
		this.listaMedicamentosSolucao  = new ArrayList<MpmItemPrescricaoMdto>(0);//limpa a lista de medicamentos da solucao
	}

	public void removerPrescricaoSolucoesSelecionadas(){

		try{

			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e) {
				LOG.error("Exceção caputada:", e);
			}
			
			int nroPrescricoesMedicamentoRemovidas = 0;
			for (MpmPrescricaoMdto prescMed: listaPrescricaoSolucoes){
				if (listaPrescricaoSolucoesSelecionadas.get(prescMed) == true){
					MpmPrescricaoMdto prescricaoMedicamentoOriginal = 
							this.prescricaoMedicaFacade.obterPrescricaoMedicamento(prescMed.getId().getAtdSeq(), prescMed.getId().getSeq());
					this.prescricaoMedicaFacade.desatachar(prescricaoMedicamentoOriginal);
					
					this.prescricaoMedicaFacade.removerPrescricaoMedicamento(this.prescricaoMedica, prescMed, nomeMicrocomputador,prescricaoMedicamentoOriginal);
					nroPrescricoesMedicamentoRemovidas++;
				}
			}
			if (nroPrescricoesMedicamentoRemovidas > 0){
				if (nroPrescricoesMedicamentoRemovidas > 1){
					apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_REMOCAO_PRESCRICAO_SOLUCOES");
				}else{
					apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_REMOCAO_PRESCRICAO_SOLUCAO");
				}
			}else{
				apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_NENHUMA_PRESCRICAO_SOLUCAO_SELECIONADA_REMOCAO");
			}
			//Limpa a tela
			this.limpar();
			listaPrescricaoSolucoes = prescricaoMedicaFacade.obterListaMedicamentosPrescritosPelaChavePrescricao(prescricaoMedicaVO.getId(), prescricaoMedicaVO.getDthrFim(), true);
			for (MpmPrescricaoMdto prescricaoMdto : listaPrescricaoSolucoes){
				listaPrescricaoSolucoesSelecionadas.put(prescricaoMdto, false);
			}
			final ComparatorChain chainSorter = new ComparatorChain();
			final BeanComparator antimicrobianoSorter = new BeanComparator("indAntiMicrobiano", new ReverseComparator(new NullComparator(false)));
			final BeanComparator descricaoSorter = new BeanComparator("descricaoFormatada", new NullComparator(false));
			chainSorter.addComparator(antimicrobianoSorter);
			chainSorter.addComparator(descricaoSorter);
			if(listaPrescricaoSolucoes != null && !listaPrescricaoSolucoes.isEmpty()) {
				Collections.sort(listaPrescricaoSolucoes, chainSorter);
			}
		}catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

	}

	public void editar(Long seq) {
		try {
			this.limpar();

			this.edicaoPrescricaoMedicamento = true;
			this.edicao = false;
			this.seq = seq;
			prescricaoMedicamento = prescricaoMedicaFacade.obterPrescricaoMedicamento(this.prescricaoMedica.getId().getAtdSeq(), seq);

			this.via = prescricaoMedicamento.getViaAdministracao();
			this.frequencia = prescricaoMedicamento.getFrequencia();
			this.qtdeHorasCorrer = prescricaoMedicamento.getQtdeHorasCorrer();
			this.unidHorasCorrer = prescricaoMedicamento.getUnidHorasCorrer(); 
			this.gotejo = prescricaoMedicamento.getGotejo();
			this.tipoVelocAdministracao = prescricaoMedicamento.getTipoVelocAdministracao(); 
			this.indBombaInfusao = prescricaoMedicamento.getIndBombaInfusao(); 
			this.observacao = prescricaoMedicamento.getObservacao();
			this.horaAdministracao = (prescricaoMedicamento.getHoraInicioAdministracao() != null)? (new SimpleDateFormat("HH:mm")).format(prescricaoMedicamento.getHoraInicioAdministracao()) :null;
			this.indSeNecessario = prescricaoMedicamento.getIndSeNecessario();
			this.tipoAprazamento = prescricaoMedicamento.getTipoFreqAprazamento();
			
			this.dataInicioTratamento = prescricaoMedicamento.getDthrInicioTratamento();
			
			if(DominioIndPendenteItemPrescricao.N.equals(prescricaoMedicamento.getIndPendente())) {
				this.listaMedicamentosSolucao = prescricaoMedicaFacade.clonarItensPrescricaoMedicamento(prescricaoMedicamento);
			} else {
				this.listaMedicamentosSolucao = prescricaoMedicamento.getItensPrescricaoMdtos();
			}

			this.numMedicamentos = (this.listaMedicamentosSolucao != null)?this.listaMedicamentosSolucao.size():0;
			this.listaMedicamentosAux = DominioQuimioterapico.P;
			this.dosePediatricaCalculada =  false;
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public void gravar(){

		try {
			
			if (this.tipoAprazamento == null) {
				apresentarMsgNegocio(Severity.ERROR,
						"CAMPO_OBRIGATORIO", "Tipo de Aprazamento");
			} else if (this.verificaRequiredFrequencia() && this.frequencia == null) {
				apresentarMsgNegocio(Severity.ERROR,
						"CAMPO_OBRIGATORIO", "Frequência");
			} else {
				this.validaDados();
				
				String nomeMicrocomputador = null;
				try {
					nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
				} catch (UnknownHostException e) {
					LOG.error("Exceção caputada:", e);
				}

				MpmPrescricaoMdto prescricaoMedicamentoOriginal = null;
				if (prescricaoMedicamento.getId() != null){
					prescricaoMedicamentoOriginal=	this.prescricaoMedicaFacade.obterPrescricaoMedicamento(prescricaoMedicamento.getId().getAtdSeq(), prescricaoMedicamento.getId().getSeq());
				this.prescricaoMedicaFacade.desatachar(prescricaoMedicamentoOriginal);
				}
				
				if(this.edicaoPrescricaoMedicamento) {
					//Se IndPendente 'N'
					if(DominioIndPendenteItemPrescricao.N.equals(prescricaoMedicamento.getIndPendente())) {
						//Gera nova prescrição medicamento
						MpmPrescricaoMdto novaPrescricaoMedicamento = prescricaoMedicaFacade.ajustaIndPendenteN(prescricaoMedicamento, this.listaMedicamentosSolucao);

						novaPrescricaoMedicamento.setPrescricaoMedica(prescricaoMedica);
						novaPrescricaoMedicamento.setViaAdministracao(this.via);
						novaPrescricaoMedicamento.setFrequencia(this.frequencia);
						novaPrescricaoMedicamento.setQtdeHorasCorrer(this.qtdeHorasCorrer);
						novaPrescricaoMedicamento.setUnidHorasCorrer(this.unidHorasCorrer);
						novaPrescricaoMedicamento.setGotejo(this.gotejo);
						novaPrescricaoMedicamento.setTipoVelocAdministracao(this.tipoVelocAdministracao);
						novaPrescricaoMedicamento.setIndBombaInfusao(this.indBombaInfusao);
						novaPrescricaoMedicamento.setObservacao(this.observacao);
						novaPrescricaoMedicamento.setIndPendente(DominioIndPendenteItemPrescricao.P);
						novaPrescricaoMedicamento.setHoraInicioAdministracao(populaDataHora(this.horaAdministracao));
						novaPrescricaoMedicamento.setIndSeNecessario(this.indSeNecessario);

						novaPrescricaoMedicamento.setDthrInicio(this.prescricaoMedicaFacade.isPrescricaoVigente(this.prescricaoMedica.getDthrInicio(),
								this.prescricaoMedica.getDthrFim()) ? this.prescricaoMedica.getDthrMovimento() : this.prescricaoMedica.getDthrInicio());
						novaPrescricaoMedicamento.setDthrFim(this.prescricaoMedica.getDthrFim());

						novaPrescricaoMedicamento.setTipoFreqAprazamento(this.tipoAprazamento);
						novaPrescricaoMedicamento.setDthrInicioTratamento(this.getDataInicioTratamento());

						if(!this.listaMedicamentosSolucao.isEmpty() && this.listaMedicamentosSolucao.size() > 1){
							novaPrescricaoMedicamento.setIndSolucao(true);
						}else{
							novaPrescricaoMedicamento.setIndSolucao(false);
						}

						for (MpmItemPrescricaoMdto itemPrescricaoMedicamento : novaPrescricaoMedicamento.getItensPrescricaoMdtos()) {
							itemPrescricaoMedicamento
							.setQtdeCalcSist24h(this.prescricaoMedicaFacade
									.buscaCalculoQuantidade24Horas(
											this.frequencia,
											novaPrescricaoMedicamento
											.getTipoFreqAprazamento() != null ? novaPrescricaoMedicamento
													.getTipoFreqAprazamento().getSeq()
													: null,
													itemPrescricaoMedicamento.getDose(),
													itemPrescricaoMedicamento.getFormaDosagem() != null ? itemPrescricaoMedicamento
															.getFormaDosagem().getSeq()
															: null,
															itemPrescricaoMedicamento.getMedicamento() != null ? itemPrescricaoMedicamento.getMedicamento()
																	.getMatCodigo() : null));

							itemPrescricaoMedicamento.setPrescricaoMedicamento(novaPrescricaoMedicamento);
						}

						this.prescricaoMedicaFacade.persistirPrescricaoMedicamento(novaPrescricaoMedicamento, nomeMicrocomputador, null);
						
						//Atualiza a atual
						prescricaoMedicamento.setIndPendente(DominioIndPendenteItemPrescricao.A);
						prescricaoMedicamento.setDthrFim(this.prescricaoMedicaFacade.isPrescricaoVigente(this.prescricaoMedica.getDthrInicio(),
								this.prescricaoMedica.getDthrFim()) ? this.prescricaoMedica.getDthrMovimento() : this.prescricaoMedica.getDthrInicio());
						prescricaoMedicamento.setServidorMovimentado(servidorLogadoFacade.obterServidorLogado());

						//prescricaoMedicamento.setItensPrescricaoMdtos(listaItensOriginal);
						this.prescricaoMedicaFacade.persistirPrescricaoMedicamento(prescricaoMedicamento, nomeMicrocomputador,prescricaoMedicamentoOriginal);					
					} else {
						prescricaoMedicamento.setPrescricaoMedica(prescricaoMedica);
						prescricaoMedicamento.setViaAdministracao(this.via);
						prescricaoMedicamento.setFrequencia(this.frequencia);
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
						
						if(!this.listaMedicamentosSolucao.isEmpty() && this.listaMedicamentosSolucao.size() > 1){
							prescricaoMedicamento.setIndSolucao(true);
						}else{
							prescricaoMedicamento.setIndSolucao(false);
						}

						for (MpmItemPrescricaoMdto itemPrescricaoMedicamento : this.listaMedicamentosSolucao) {
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
													itemPrescricaoMedicamento.getDose(),
													itemPrescricaoMedicamento.getFormaDosagem() != null ? itemPrescricaoMedicamento
															.getFormaDosagem().getSeq()
															: null,
															itemPrescricaoMedicamento.getMedicamento() != null ? itemPrescricaoMedicamento.getMedicamento()
																	.getMatCodigo() : null));

							itemPrescricaoMedicamento.setPrescricaoMedicamento(prescricaoMedicamento);
						}

						prescricaoMedicamento.setItensPrescricaoMdtos(this.listaMedicamentosSolucao);

						this.prescricaoMedicaFacade.persistirPrescricaoMedicamento(prescricaoMedicamento, nomeMicrocomputador,prescricaoMedicamentoOriginal);
					}
				} else {

					MpmPrescricaoMdtoId id = new MpmPrescricaoMdtoId();
					id.setAtdSeq(this.prescricaoMedicaVO.getId().getAtdSeq());					

					prescricaoMedicamento.setId(id);
					prescricaoMedicamento.setPrescricaoMedica(prescricaoMedica);
					prescricaoMedicamento.setViaAdministracao(this.via);
					prescricaoMedicamento.setFrequencia(this.frequencia);
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

					prescricaoMedicamento.setDthrInicio(this.prescricaoMedicaFacade.isPrescricaoVigente(this.prescricaoMedica.getDthrInicio(),
							this.prescricaoMedica.getDthrFim()) ? this.prescricaoMedica.getDthrMovimento() : this.prescricaoMedica.getDthrInicio());
					prescricaoMedicamento.setDthrFim(this.prescricaoMedica.getDthrFim());
					prescricaoMedicamento.setTipoFreqAprazamento(this.tipoAprazamento);
					prescricaoMedicamento.setDthrInicioTratamento(this.getDataInicioTratamento());

					if(!this.listaMedicamentosSolucao.isEmpty() && this.listaMedicamentosSolucao.size() > 1){
						prescricaoMedicamento.setIndSolucao(true);
					}else{
						prescricaoMedicamento.setIndSolucao(false);
					}

					for (MpmItemPrescricaoMdto itemPrescricaoMedicamento : this.listaMedicamentosSolucao) {
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
												itemPrescricaoMedicamento.getDose(),
												itemPrescricaoMedicamento.getFormaDosagem() != null ? itemPrescricaoMedicamento
														.getFormaDosagem().getSeq()
														: null,
														itemPrescricaoMedicamento.getMedicamento() != null ? itemPrescricaoMedicamento.getMedicamento()
																.getMatCodigo() : null));

						itemPrescricaoMedicamento.setPrescricaoMedicamento(prescricaoMedicamento);
					}

					prescricaoMedicamento.setItensPrescricaoMdtos(this.listaMedicamentosSolucao);

					this.prescricaoMedicaFacade.persistirPrescricaoMedicamento(prescricaoMedicamento, nomeMicrocomputador,prescricaoMedicamentoOriginal);
				}

				if(edicaoPrescricaoMedicamento) {
					apresentarMsgNegocio(Severity.INFO,
							ManterPrescricaoSolucaoControllerExceptionCode.PRESCRICAO_SOLUCAO_ALTERADA_SUCESSO.toString());
				} else {
					apresentarMsgNegocio(Severity.INFO,
							ManterPrescricaoSolucaoControllerExceptionCode.PRESCRICAO_SOLUCAO_INSERIDA_SUCESSO.toString());

				}

				this.limpar();
				listaPrescricaoSolucoes = prescricaoMedicaFacade.obterListaMedicamentosPrescritosPelaChavePrescricao(prescricaoMedicaVO.getId(), prescricaoMedicaVO.getDthrFim(), true);
				for (MpmPrescricaoMdto prescricaoMdto : listaPrescricaoSolucoes){
					listaPrescricaoSolucoesSelecionadas.put(prescricaoMdto, false);
				}
				final ComparatorChain chainSorter = new ComparatorChain();
				final BeanComparator antimicrobianoSorter = new BeanComparator("indAntiMicrobiano", new ReverseComparator(new NullComparator(false)));
				final BeanComparator descricaoSorter = new BeanComparator("descricaoFormatada", new NullComparator(false));
				chainSorter.addComparator(antimicrobianoSorter);
				chainSorter.addComparator(descricaoSorter);
				if(listaPrescricaoSolucoes != null && !listaPrescricaoSolucoes.isEmpty()) {
					Collections.sort(listaPrescricaoSolucoes, chainSorter);
				}
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (BaseRuntimeException e) {
			apresentarExcecaoNegocio(e);
		}
		verificarExibicaoModalProtocolo();
	}

	@SuppressWarnings("PMD.NPathComplexity")
	protected void validaDados() throws BaseException {


		if(this.medicamento != null) {
			throw new ApplicationBusinessException(
					ManterPrescricaoSolucaoControllerExceptionCode.MEDICAMENTO_SELECIONADO_CRUD, medicamento.getDescricaoEditada());
		}

		if(this.listaMedicamentosSolucao == null || this.listaMedicamentosSolucao.isEmpty()){
			throw new ApplicationBusinessException(
					ManterPrescricaoSolucaoControllerExceptionCode.LISTA_MEDICAMENTOS_SOLUCAO_VAZIA);
		}

		if(this.via == null){
			throw new ApplicationBusinessException(
					ManterPrescricaoMedicamentoExceptionCode.OBRIGATORIO_PREENCHER_VIA);			
		}

		Boolean validarBombaInf = this.unidadeFuncional != null;

		//Verificacoes dos itensPrescricaoMedicamento
		for (MpmItemPrescricaoMdto itemPrescricaoMedicamento : this.listaMedicamentosSolucao) {
			AfaMedicamento med = itemPrescricaoMedicamento.getMedicamento();

			if(validarBombaInf) {
				if(this.indBombaInfusao) {
					if(prescricaoMedicaFacade.validaBombaInfusao(this.unidadeFuncional, this.via, med)) {
						validarBombaInf = false;
					}
				} else {
					validarBombaInf = false;
				}
			}

			if (validarBombaInf && this.indBombaInfusao && !prescricaoMedicaFacade.validaBombaInfusao(this.unidadeFuncional, this.via, med)) {
				throw new ApplicationBusinessException(
						ManterPrescricaoSolucaoControllerExceptionCode.MSG_0336_UNIDADE_NAO_PERMITE_BOMBA_DE_INFUSAO_NA_PRESCRICAO);
			}

			if (med.getIndExigeObservacao()	&& StringUtils.isBlank(itemPrescricaoMedicamento.getObservacao())) {
				throw new ApplicationBusinessException(
						ManterPrescricaoSolucaoControllerExceptionCode.OBRIGATORIO_PREENCHER_COMPLEMENTO_MEDICAMENTO_SOLUCAO);
			}

			if (itemPrescricaoMedicamento.getDose() != null && itemPrescricaoMedicamento.getFormaDosagem() == null) {
				throw new ApplicationBusinessException(
						ManterPrescricaoSolucaoControllerExceptionCode.OBRIGATORIO_PREENCHER_UNIDADE_DOSAGEM_AO_PREENCHER_DOSE_MEDICAMENTO_SOLUCAO, itemPrescricaoMedicamento.getMedicamento().getDescricaoEditada());
			}

		}//fim do for

		if (validarBombaInf) {
			throw new ApplicationBusinessException(
					ManterPrescricaoSolucaoControllerExceptionCode.MSG_0336_UNIDADE_NAO_PERMITE_BOMBA_DE_INFUSAO_NA_PRESCRICAO);
		}

		//Verificacoes do prescricaoMedicamento
		if (verificaRequiredFrequencia() && this.frequencia == null) {
			throw new ApplicationBusinessException(
					ManterPrescricaoSolucaoControllerExceptionCode.OBRIGATORIO_PREENCHER_FREQUENCIA_PARA_ESTE_APRAZAMENTO);
		}

		if (this.qtdeHorasCorrer != null && this.unidHorasCorrer == null) {
			throw new ApplicationBusinessException(
					ManterPrescricaoSolucaoControllerExceptionCode.OBRIGATORIO_PREENCHER_UNIDADE_AO_PREENCHER_CORRER_EM);
		}

		if (this.qtdeHorasCorrer == null && this.unidHorasCorrer != null) {
			throw new ApplicationBusinessException(
					ManterPrescricaoSolucaoControllerExceptionCode.OBRIGATORIO_PREENCHER_CORRER_EM_AO_PREENCHER_UNIDADE);
		}
		
		if(BigDecimal.ZERO.equals(this.gotejo)){
			this.gotejo = null;
		}

		if (this.gotejo != null && this.tipoVelocAdministracao == null) {
			throw new ApplicationBusinessException(
					ManterPrescricaoSolucaoControllerExceptionCode.OBRIGATORIO_PREENCHER_UNIDADE_INFUSAO_AO_PREENCHER_VELOCIDADE_INFUSAO);
		}		

		validarDataInicioTratamento();
	}

	private void validarDataInicioTratamento() throws BaseException {
		if (this.dataInicioTratamento != null && !this.getEdicaoPrescricaoMedicamento()) {
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
		Date dtNascimentoPaciente1 = 
			getPrescricaoMedicaVO().getPrescricaoMedica().getAtendimento().
			getPaciente().getDtNascimento();

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

	public boolean medicamentoSelecionadoAntimicrobiano() {
		boolean retorno = false;
		retorno = temAntimicrobianoListaMedicamentosSolucao();
		if (retorno && getDataInicioTratamento() == null) {
			try {
				setDataInicioTratamento(prescricaoMedicaFacade.atualizaInicioTratamento(getDataHoraInicio(), populaDataHora(this.horaAdministracao)));
			} catch (ApplicationBusinessException e) {
				this.apresentarExcecaoNegocio(e);
			}
		}
		return retorno;
	}

	/**
	 * Verifica se algum registro da lista de medicamentos possui antimicrobiano
	 * @return
	 */
	private boolean temAntimicrobianoListaMedicamentosSolucao() {
		boolean retorno = false;
		if (getListaMedicamentosSolucao() != null) {
			for (MpmItemPrescricaoMdto solucao : getListaMedicamentosSolucao()) {
				if (solucao.getIndAntiMicrobiano()) {
					retorno = true;
					break;
				}
			}
		}
		
		if (!retorno) {
			this.dataInicioTratamento = null;
		}
		
		return retorno;
	}

	private Date getDataHoraInicio() throws ApplicationBusinessException {
		return (this.prescricaoMedicaFacade.isPrescricaoVigente(this.prescricaoMedica.getDthrInicio(),
				this.prescricaoMedica.getDthrFim()) ? this.prescricaoMedica.getDthrMovimento() : this.prescricaoMedica.getDthrInicio());
	}

	/**
	 * Recebe uma String hora (formato HH:mm) e retorna uma data que representa
	 * esta hora.
	 * 
	 * @param hora
	 * @return
	 * @throws ApplicationBusinessException
	 */
	protected Date populaDataHora(String hora) throws ApplicationBusinessException {
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
			throw new ApplicationBusinessException(
					ManterPrescricaoMedicamentoExceptionCode.HORA_INVALIDA);
		}
	}

	public void limparCamposRelacionados(){
		this.medicamentoVO = null;
		this.medicamento = null;
		this.via = null;
		this.unidade = null;
		this.unidadeDosagem = null;
		this.dose = null;
		this.informacoesFarmacologicas = null;
		this.dataInicioTratamento = null;
	}

	protected void verificarExisteMensagemCadastrada(){
		try{
			if(DominioPadronizado.N.equals(this.listaMedicamentosAux)){
				AghParametros p = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_MSG_MED_NAO_CADASTRADO);

				String str = StringUtils.replace(StringUtils.replace(StringUtils.replace(p.getVlrTexto(), "#3",(this.medicamento.getMpmUnidadeMedidaMedicas() != null)?this.medicamento.getMpmUnidadeMedidaMedicas().getDescricao():""), "#2", (this.medicamento.getConcentracao() != null)?this.medicamento.getConcentracaoFormatada():"0"), "#1", this.medicamento.getDescricao());

				this.apresentarMsgNegocio(Severity.INFO, str);
			}

		}catch (ApplicationBusinessException e) {
			//Caso o parâmetro não seja encontrado, a princípio, para essa situação em específico, nada deve acontecer.
			LOG.error(e.getMessage(), e);
		}
	}

	public void realizarVerificacoesMedicamento(){
		if (this.medicamentoVO != null) {
			this.medicamentoVO.setMedicamento(farmaciaFacade.obterMedicamento(this.getMedicamentoVO().getMatCodigo()));

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

			this.medicamento = farmaciaFacade.obterMedicamento(medicamentoVO.getMatCodigo());

			this.verificarExisteMensagemCadastrada();
			List<Integer> medMatCodigoList = new ArrayList<Integer>(); 
			medMatCodigoList.add(this.medicamento.getMatCodigo());

			//this.medicamento = this.farmaciaFacade.obterMedicamentoPorId(this.medicamento.getMatCodigo());
			if (!this.medicamento.getIndPermiteDoseFracionada()
					&& this.dose != null && !(this.dose.stripTrailingZeros().scale() <= 0)) {
				apresentarMsgNegocio(Severity.ERROR,
						ManterPrescricaoSolucaoControllerExceptionCode.MPM_01128.toString());
				this.dose = null;
			}

			this.informacoesFarmacologicas = this.farmaciaFacade.obterInfromacoesFarmacologicas(medicamento);

			listaDosagens = this.prescricaoMedicaFacade.buscarDosagensMedicamento(this.medicamento.getMatCodigo());
			//VERIFICA UNIDADE MEDIDA PADRÃO PARA O MEDICAMENTO
			if(!listaDosagens.isEmpty()){
				if(listaDosagens.size() == 1){
					this.unidadeDosagem = listaDosagens.get(0);//SE só existe uma dosagem possivel para o medicamento, esta deve vir selecionada
				}else{
					AfaFormaDosagem formaDosagem = this.farmaciaFacade.buscarDosagenPadraoMedicamento(this.medicamento.getMatCodigo());
					if(formaDosagem != null){
						for(int i = 0; i < listaDosagens.size(); i++){
							if(formaDosagem.getSeq().equals(listaDosagens.get(i).getFormaDosagem().getSeq())){
								this.unidadeDosagem = listaDosagens.get(i);
							}
						}
					}
				}
			}
			verificarExibicaoModalProtocolo();
		}
	}
	
	public void verificarExibicaoModalProtocolo(){
		if(medicamento != null && medicamento.getLinkProtocoloUtilizacao() != null &&  !StringUtils.isBlank(medicamento.getLinkProtocoloUtilizacao())){
			this.openDialog("modalProtocoloWG");
		}
	}

	public void verificarDose() {
		if (this.dose != null && this.dose.doubleValue() <= 0) {
			this.apresentarMsgNegocio(Severity.ERROR, ManterPrescricaoSolucaoControllerExceptionCode.DOSE_PRECISA_SER_MAIOR_QUE_ZERO.toString());
		}
	}
	
	public void validarUnidadeDsPed() throws BaseException  {
		if((this.dosePediatricaCalculada) && (!CoreUtil.igual(unidadeDosagemPediatrica, this.unidadeDosagem.getFormaDosagem().getUnidadeMedidaMedicas()))) {
			throw new ApplicationBusinessException(
					ManterPrescricaoSolucaoControllerExceptionCode.UNIDADE_CALCULO_DOSE_PED_DV_SER_IGUAL_UNIDADE_MED);
		}
	}
	
	private boolean validarObrigatoriosAdicaoMedicamento(Boolean veioModeloBasico){
	

		if (!veioModeloBasico) {
			if ((this.dosePediatricaCalculada)
					&& (!CoreUtil.igual(unidadeDosagemPediatrica,
							this.unidadeDosagem.getFormaDosagem()
									.getUnidadeMedidaMedicas()))) {
				apresentarMsgNegocio(Severity.ERROR,
						"UNIDADE_CALCULO_DOSE_PED_DV_SER_IGUAL_UNIDADE_MED");
				return true;
			}
		}
		return false;
	}
	
	public void adicionarMedicamentoModeloBasico(){
		if (validarObrigatoriosAdicaoMedicamento(true)){
			return;
		}
		
		continuarAdicionandoMedicamento();
	}

	public String adicionarMedicamento() {
		
		if (validarObrigatoriosAdicaoMedicamento(false)){
			return null;
		}
		
		return continuarAdicionandoMedicamento();
	}

	private String continuarAdicionandoMedicamento() {
		
		if (validarObrigatoriosAdicaoMedicamento(false)){
			return null;
		}
		
		try {
			this.prescricaoMedicaFacade.verificaDoseFracionada(medicamento.getMatCodigo(), dose,
					this.unidadeDosagem.getFormaDosagem().getSeq());

			boolean isCadastrado = false;
			this.setEdicao(false);
			matCodigo = null;

			
			isCadastrado = validarListaMedicamentosSolucao();

			if(!isCadastrado) {
				MpmItemPrescricaoMdto medicamentoSolucao = new MpmItemPrescricaoMdto();
				medicamentoSolucao.setMedicamento(farmaciaFacade.obterMedicamento(medicamento.getMatCodigo()));
				medicamentoSolucao.setDose(dose);
				// Comentado devido ao bug registrado em http://qos-aghu.mec.gov.br/mantis/view.php?id=356
				//				if(this.unidadeDosagem != null && this.unidadeDosagem.getFdsUmmSeq() != null) {
				//					medicamentoSolucao.setUnidadeMedidaMedica(this.prescricaoMedicaFacade
				//						.obterUnidadeMedicaPorId(this.unidadeDosagem.getFdsUmmSeq()));
				//				}
				medicamentoSolucao.setObservacao((StringUtils.isEmpty(complemento) || complemento == null)?null:complemento);
				medicamentoSolucao.setFormaDosagem(this.unidadeDosagem
						.getFormaDosagem());
				medicamentoSolucao.setMdtoAguardaEntrega(false);
				medicamentoSolucao.setOrigemJustificativa(false);	

				medicamentoSolucao.setDoseCalculada(this.doseCalculada!=null?this.doseCalculada.doubleValue():null);
				medicamentoSolucao.setTipoCalculoDose(this.tipoCalculoDose);
				medicamentoSolucao.setQtdeParamCalculo(this.dosePediatrica!=null?this.dosePediatrica.doubleValue():null);
				medicamentoSolucao.setBaseParamCalculo(this.unidadeBaseParametroCalculo);
				medicamentoSolucao.setParamCalculoPrescricao(this.parametroCalculo);
				medicamentoSolucao.setUnidadeMedidaMedica(this.unidadeDosagemPediatrica);
				medicamentoSolucao.setUnidDuracaoCalculo(unidadeTempo);
				medicamentoSolucao.setDuracaoParamCalculo(duracao);

				//TODO - AJUSTAR VALORES CÁLCULO DOSE PEDIÁTRICA
				
				listaMedicamentosSolucao.add(medicamentoSolucao);

				medicamento = null;
				medicamentoVO = null;
				unidade = null;
				unidadeDosagem = null;
				dose = null;
				complemento = null;
				informacoesFarmacologicas = null;
				this.doseCalculada = null;
				this.dosePediatrica = null;
				this.unidadeDosagemPediatrica = null;
				this.unidadeBaseParametroCalculo = null;
				this.tipoCalculoDose = null;
				this.duracao = null;
				this.unidadeTempo = null;
				this.viaDosePed = null;
				this.frequenciaDosePed = null;
				this.tipoAprazamentoDosePed = null;
				this.exibirCalculoDosePediatrica = false;
				this.listaMedicamentosAux = DominioQuimioterapico.P;
				this.dosePediatricaCalculada =  false;
			}

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (BaseRuntimeException e) {
			apresentarExcecaoNegocio(e);
		}
		
		return null;
	}

	private boolean validarListaMedicamentosSolucao() {
		for (MpmItemPrescricaoMdto item : listaMedicamentosSolucao) {
			if (item.getMedicamento().getMatCodigo()
					.equals(medicamento.getMatCodigo())) {
				this.apresentarMsgNegocio(
						Severity.ERROR, "Medicamento " + medicamento.getDescricaoEditada() + " já adicionado a solução.");
				dose = null;
				complemento = null;
				unidade = null;
				medicamento = null;
				this.medicamentoVO = null;
				return true;
			}
		}
		
		return false;
	}

	private boolean validarListaMedicamentosSolucaoComparandoIndice() {
		if(!listaMedicamentosSolucao.isEmpty()) {
			int indiceAux = 0;		
			for (MpmItemPrescricaoMdto item : listaMedicamentosSolucao) {
				indiceAux++;
				if(item.getMedicamento().getMatCodigo().equals(medicamento.getMatCodigo()) && indice != indiceAux) {
					this.apresentarMsgNegocio(Severity.ERROR, "Medicamento " + medicamento.getDescricaoEditada() + " já adicionado a solução.");
					return true;
				}
			}
		}
		return false;
	}

	public void alterarMedicamento() {
		try {

			validarUnidadeDsPed();
			
			this.prescricaoMedicaFacade.verificaDoseFracionada(medicamento.getMatCodigo(), dose,
					this.unidadeDosagem.getFormaDosagem().getSeq());

			boolean isCadastrado = false;
			this.setEdicao(false);
			matCodigo = null;

			isCadastrado = validarListaMedicamentosSolucaoComparandoIndice();

			if(!isCadastrado) {
				listaMedicamentosSolucao.get(indice - 1).setMedicamento(farmaciaFacade.obterMedicamento(medicamento.getMatCodigo()));
				listaMedicamentosSolucao.get(indice - 1).setDose(dose);
				// Comentado devido ao bug registrado em http://qos-aghu.mec.gov.br/mantis/view.php?id=356
				//				if(this.unidadeDosagem != null && this.unidadeDosagem.getFdsUmmSeq() != null) { 
				//					listaMedicamentosSolucao.get(indice - 1).setUnidadeMedidaMedica(this.prescricaoMedicaFacade
				//							.obterUnidadeMedicaPorId(this.unidadeDosagem.getFdsUmmSeq()));
				//				} else {
				listaMedicamentosSolucao.get(indice - 1).setUnidadeMedidaMedica(null);
				//				}
				listaMedicamentosSolucao.get(indice - 1).setFormaDosagem(this.unidadeDosagem
						.getFormaDosagem());
				listaMedicamentosSolucao.get(indice - 1).setObservacao((StringUtils.isEmpty(complemento) || complemento == null)?null:complemento);

				listaMedicamentosSolucao.get(indice - 1).setDoseCalculada(this.doseCalculada!=null?this.doseCalculada.doubleValue():null);
				listaMedicamentosSolucao.get(indice - 1).setTipoCalculoDose(this.tipoCalculoDose);
				listaMedicamentosSolucao.get(indice - 1).setQtdeParamCalculo(this.dosePediatrica!=null?this.dosePediatrica.doubleValue():null);
				listaMedicamentosSolucao.get(indice - 1).setBaseParamCalculo(this.unidadeBaseParametroCalculo);
				listaMedicamentosSolucao.get(indice - 1).setParamCalculoPrescricao(this.parametroCalculo);
				listaMedicamentosSolucao.get(indice - 1).setUnidDuracaoCalculo(unidadeTempo);
				listaMedicamentosSolucao.get(indice - 1).setDuracaoParamCalculo(duracao);
				listaMedicamentosSolucao.get(indice - 1).setUnidadeMedidaMedica(this.unidadeDosagemPediatrica);
				
				medicamento = null;
				medicamentoVO = null;
				unidade = null;
				dose = null;
				complemento = null;
				unidade = null;
				unidadeDosagem = null;
				this.doseCalculada = null;
				this.dosePediatrica = null;
				this.unidadeDosagemPediatrica = null;
				this.unidadeBaseParametroCalculo = null;
				this.tipoCalculoDose = null;
				this.duracao = null;
				this.unidadeTempo = null;
				this.viaDosePed = null;
				this.frequenciaDosePed = null;
				this.tipoAprazamentoDosePed = null;
				this.exibirCalculoDosePediatrica = false;				
				this.listaMedicamentosAux = DominioQuimioterapico.P;
				this.informacoesFarmacologicas = null;
			}

		} catch (BaseException e) {
			e.getMessage();
			apresentarExcecaoNegocio(e);
		}
	}

	public void removerMedicamento(MpmItemPrescricaoMdto item) {
		this.setEdicao(false);
		matCodigo = null;
		for (Iterator<MpmItemPrescricaoMdto> i = listaMedicamentosSolucao.iterator(); i.hasNext(); ) {
			MpmItemPrescricaoMdto itemPrs = (MpmItemPrescricaoMdto)i.next();
			if(itemPrs.getMedicamento().getMatCodigo().equals(item.getMedicamento().getMatCodigo())) {
				// TODO falta implementar o método doRemoverMedicamento 
				//this.doRemoverMedicamento(itemPrs);
				i.remove();
				break;
			}
		}
	}

	private Boolean isDosePed(MpmItemPrescricaoMdto itemPrs) {
		return itemPrs.getDoseCalculada()!= null? true : false;
	}
	
	/**
	 * Metodo gancho.<br>
	 * Deve ser chamado apenas quando for remover um item de Medicament da lista.<br>
	 * Responsabilidade da Sub-classe.
	 * 
	 * @param itemPrs
	 *
	protected void doRemoverMedicamento(MpmItemPrescricaoMdto itemPrs) {
	} */

	public void editarMedicamento(MpmItemPrescricaoMdto item) {
		this.setEdicao(true);
		indice = 0;
		for (Iterator<MpmItemPrescricaoMdto> i = listaMedicamentosSolucao.iterator(); i.hasNext(); ) {
			MpmItemPrescricaoMdto itemPrs = (MpmItemPrescricaoMdto)i.next();
			indice++;
			if(itemPrs.getMedicamento().getMatCodigo().equals(item.getMedicamento().getMatCodigo())) {
				matCodigo = itemPrs.getMedicamento().getMatCodigo();
				dose = itemPrs.getDose();
				complemento = itemPrs.getObservacao();
				medicamento = farmaciaFacade.obterMedicamento(itemPrs.getMedicamento().getMatCodigo());
				medicamentoVO = new MedicamentoVO();
				medicamentoVO.setMatCodigo(medicamento.getMatCodigo());
				medicamentoVO.setMedicamento(medicamento);				
				unidadeDosagem = prescricaoMedicaFacade.obterVMpmDosagem(this.medicamento.getMatCodigo(), itemPrs.getFormaDosagem().getSeq());

				populaFieldsMpmItemPrescricaoMdto(itemPrs);
				
				StringBuilder returnValue = popularFieldsMedicamento();
				if(medicamento.getMpmUnidadeMedidaMedicas() != null){
					returnValue.append(this.medicamento.getMpmUnidadeMedidaMedicas().getDescricao());
				}
				medicamentoVO.setDescricaoEditada(returnValue.toString());
				
				this.informacoesFarmacologicas = this.farmaciaFacade.obterInfromacoesFarmacologicas(medicamento);

				break;
			}
		}
	}

	private StringBuilder popularFieldsMedicamento() {
		StringBuilder returnValue = new StringBuilder("");
		if (medicamento.getDescricao()!= null) {
			returnValue.append(medicamento.getDescricao());
			returnValue.append(' ');
		}
		if(medicamento.getConcentracao() != null){
			Locale locBR = new Locale("pt", "BR");//Brasil 
			DecimalFormatSymbols dfSymbols = new DecimalFormatSymbols(locBR);
			dfSymbols.setDecimalSeparator(',');
			DecimalFormat format;
			if(this.medicamento.getConcentracao() != null && this.medicamento.getConcentracao().stripTrailingZeros().scale() <= 0)
			{
				format = new DecimalFormat("#,###,###,###,##0.###############", dfSymbols);
				returnValue.append(format.format(this.medicamento.getConcentracao()));
				returnValue.append(' ');
			} else if (this.medicamento.getConcentracao() != null) {
				format = new DecimalFormat("#,###,###,###,##0.0##############", dfSymbols);
				returnValue.append(format.format(this.medicamento.getConcentracao()));
				returnValue.append(' ');
			}
		}
		return returnValue;
	}

	private void populaFieldsMpmItemPrescricaoMdto(MpmItemPrescricaoMdto itemPrs) {
		if(itemPrs != null) {
			this.dosePediatrica = itemPrs.getQtdeParamCalculo()!= null? BigDecimal.valueOf(itemPrs.getQtdeParamCalculo()):null; 
			this.unidadeBaseParametroCalculo = itemPrs.getBaseParamCalculo();
			this.tipoCalculoDose = itemPrs.getTipoCalculoDose();
			this.duracao = itemPrs.getDuracaoParamCalculo();
			this.unidadeTempo = itemPrs.getUnidDuracaoCalculo();
			this.doseCalculada = itemPrs.getDoseCalculada()!= null? BigDecimal.valueOf(itemPrs.getDoseCalculada()):null;
			this.unidadeDosagemPediatrica = itemPrs.getUnidadeMedidaMedica();
			this.frequenciaDosePed = this.frequencia;
			this.tipoAprazamentoDosePed = this.tipoAprazamento;
			this.viaDosePed = this.via;
			this.dosePediatricaCalculada =  isDosePed(itemPrs);
		}
	}

	public void cancelarEdiMed() {
		this.setEdicao(false);
		matCodigo = null;
		medicamentoVO = null;
		medicamento = null;
		unidade = null;
		unidadeDosagem = null;
		dose = null;
		complemento = null;
		this.doseCalculada = null;
		this.dosePediatrica = null;
		this.unidadeDosagemPediatrica = null;
		this.unidadeBaseParametroCalculo = null;
		this.tipoCalculoDose = null;
		this.duracao = null;
		this.unidadeTempo = null;
		this.viaDosePed = null;
		this.frequenciaDosePed = null;
		this.tipoAprazamentoDosePed = null;
		this.exibirCalculoDosePediatrica = false;
		this.listaMedicamentosAux = DominioQuimioterapico.P;
		this.informacoesFarmacologicas = null;
	}

	public boolean verificarSeDoseFracionaria(Object valor){
		boolean retorno = true;
		if(valor != null){
			if(((BigDecimal)valor).stripTrailingZeros().scale() <= 0){
				retorno = false;
			}
		}
		return retorno;
	}


	public String obtemDescricaoUnidadeDosagem(Integer matCodigo, Integer seqFormaDosagem) {
		if(matCodigo != null && seqFormaDosagem != null) {
			VMpmDosagem dosagem = prescricaoMedicaFacade.obterVMpmDosagem(matCodigo, seqFormaDosagem);
			return dosagem != null ? dosagem.getSiglaUnidadeMedidaMedica() : null;
		}
		return null;
	}

	public String cancelar() {

		if (this.medicamentoVO != null || (this.listaMedicamentosSolucao != null && this.listaMedicamentosSolucao.size() != numMedicamentos)) {
			this.openDialog("modalConfirmacaoPendenciaWG");
			return null;
		}

		return this.voltar();
	}
	
	public void cancelarModalPesoAltura(){
		this.tipoMedicao = null;
		this.peso = null;
		this.altura = null;
		this.sc = null;
		this.closeDialog("modalPesoAlturaWG");
	}

	public String voltar() {
		this.prescricaoMedicaVO = null;
		this.limpar();
		return PAGINA_MANTER_PRESCRICAO_MEDICA;
	}

	public PrescricaoMedicaVO getPrescricaoMedicaVO() {
		return prescricaoMedicaVO;
	}

	public void setPrescricaoMedicaVO(PrescricaoMedicaVO prescricaoMedicaVO) {
		this.prescricaoMedicaVO = prescricaoMedicaVO;
	}

	public List<MpmPrescricaoMdto> getListaPrescricaoSolucoes() {
		return listaPrescricaoSolucoes;
	}

	public void setListaPrescricaoSolucoes(
			List<MpmPrescricaoMdto> listaPrescricaoSolucoes) {
		this.listaPrescricaoSolucoes = listaPrescricaoSolucoes;
	}

	public Map<MpmPrescricaoMdto, Boolean> getListaPrescricaoSolucoesSelecionadas() {
		return listaPrescricaoSolucoesSelecionadas;
	}

	public void setListaPrescricaoSolucoesSelecionadas(
			Map<MpmPrescricaoMdto, Boolean> listaPrescricaoSolucoesSelecionadas) {
		this.listaPrescricaoSolucoesSelecionadas = listaPrescricaoSolucoesSelecionadas;
	}

	public AfaViaAdministracao getVia() {
		return via;
	}

	public void setVia(AfaViaAdministracao via) {
		this.via = via;
	}

	public Boolean getTodasAsVias() {
		return todasAsVias;
	}

	public void setTodasAsVias(Boolean todasAsVias) {
		this.todasAsVias = todasAsVias;
	}

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}


	public AfaMedicamento getMedicamento() {
		return medicamento;
	}

	public void setMedicamento(AfaMedicamento medicamento) {
		this.medicamento = medicamento;
	}

	public String getMensagemExibicaoModal() {
		return mensagemExibicaoModal;
	}

	public void setMensagemExibicaoModal(String mensagemExibicaoModal) {
		this.mensagemExibicaoModal = mensagemExibicaoModal;
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

	public DominioUnidadeHorasMinutos getUnidHorasCorrer() {
		return unidHorasCorrer;
	}

	public void setUnidHorasCorrer(DominioUnidadeHorasMinutos unidHorasCorrer) {
		this.unidHorasCorrer = unidHorasCorrer;
	}

	public Short getQtdeHorasCorrer() {
		return qtdeHorasCorrer;
	}

	public void setQtdeHorasCorrer(Short qtdeHorasCorrer) {
		this.qtdeHorasCorrer = qtdeHorasCorrer;
	}

	public AfaTipoVelocAdministracoes getTipoVelocAdministracao() {
		return tipoVelocAdministracao;
	}

	public void setTipoVelocAdministracao(
			AfaTipoVelocAdministracoes tipoVelocAdministracao) {
		this.tipoVelocAdministracao = tipoVelocAdministracao;
	}

	public BigDecimal getGotejo() {
		return gotejo;
	}

	public void setGotejo(BigDecimal gotejo) {
		this.gotejo = gotejo;
	}

	public Boolean getIndBombaInfusao() {
		return indBombaInfusao;
	}

	public void setIndBombaInfusao(Boolean indBombaInfusao) {
		this.indBombaInfusao = indBombaInfusao;
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

	public VMpmDosagem getUnidadeDosagem() {
		return unidadeDosagem;
	}

	public void setUnidadeDosagem(VMpmDosagem unidadeDosagem) {
		this.unidadeDosagem = unidadeDosagem;
	}

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

	public List<VMpmDosagem> getListaDosagens() {
		return listaDosagens;
	}

	public void setListaDosagens(List<VMpmDosagem> listaDosagens) {
		this.listaDosagens = listaDosagens;
	}

	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento != null ? complemento.toUpperCase() : null;
	}

	public List<MpmItemPrescricaoMdto> getListaMedicamentosSolucao() {
		return listaMedicamentosSolucao;
	}

	public void setListaMedicamentosSolucao(
			List<MpmItemPrescricaoMdto> listaMedicamentosSolucao) {
		this.listaMedicamentosSolucao = listaMedicamentosSolucao;
	}

	public Boolean getEdicaoPrescricaoMedicamento() {
		return edicaoPrescricaoMedicamento;
	}

	public void setEdicaoPrescricaoMedicamento(Boolean edicaoPrescricaoMedicamento) {
		this.edicaoPrescricaoMedicamento = edicaoPrescricaoMedicamento;
	}

	public Boolean getEdicao() {
		return edicao;
	}

	public void setEdicao(Boolean edicao) {
		this.edicao = edicao;
	}

	public Integer getMatCodigo() {
		return matCodigo;
	}

	public void setMatCodigo(Integer matCodigo) {
		this.matCodigo = matCodigo;
	}

	public MpmPrescricaoMedica getPrescricaoMedica() {
		return prescricaoMedica;
	}

	public void setPrescricaoMedica(MpmPrescricaoMedica prescricaoMedica) {
		this.prescricaoMedica = prescricaoMedica;
	}

	public IFarmaciaFacade getFarmaciaFacade() {
		return farmaciaFacade;
	}

	public MedicamentoVO getMedicamentoVO() {
		return medicamentoVO;
	}

	public void setMedicamentoVO(MedicamentoVO medicamentoVO) {
		this.medicamentoVO = medicamentoVO;
	}

	public Long getSeq() {
		return seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}

	public int getIdConversacaoAnterior() {
		return idConversacaoAnterior;
	}

	public void setIdConversacaoAnterior(int idConversacaoAnterior) {
		this.idConversacaoAnterior = idConversacaoAnterior;
	}

	public Date getDataInicioTratamento() {
		return dataInicioTratamento;
	}

	public void setDataInicioTratamento(Date dataInicioTratamento) {
		this.dataInicioTratamento = dataInicioTratamento;
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

	public BigDecimal getDoseCalculada() {
		return doseCalculada;
	}

	public void setDoseCalculada(BigDecimal doseCalculada) {
		this.doseCalculada = doseCalculada;
	}

	public DominioTipoMedicaoPeso getTipoMedicao() {
		return tipoMedicao;
	}

	public void setTipoMedicao(DominioTipoMedicaoPeso tipoMedicao) {
		this.tipoMedicao = tipoMedicao;
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

	public Integer getDuracao() {
		return duracao;
	}

	public void setDuracao(Integer duracao) {
		this.duracao = duracao;
	}

	public DominioDuracaoCalculo getUnidadeTempo() {
		return unidadeTempo;
	}

	public void setUnidadeTempo(DominioDuracaoCalculo unidadeTempo) {
		this.unidadeTempo = unidadeTempo;
	}

	public AfaViaAdministracao getViaDosePed() {
		return viaDosePed;
	}

	public void setViaDosePed(AfaViaAdministracao viaDosePed) {
		this.viaDosePed = viaDosePed;
	}

	public Short getFrequenciaDosePed() {
		return frequenciaDosePed;
	}

	public void setFrequenciaDosePed(Short frequenciaDosePed) {
		this.frequenciaDosePed = frequenciaDosePed;
	}

	public MpmTipoFrequenciaAprazamento getTipoAprazamentoDosePed() {
		return tipoAprazamentoDosePed;
	}

	public void setTipoAprazamentoDosePed(
			MpmTipoFrequenciaAprazamento tipoAprazamentoDosePed) {
		this.tipoAprazamentoDosePed = tipoAprazamentoDosePed;
	}

	public Integer getMatCodigoMedicamentoEdicao() {
		return matCodigoMedicamentoEdicao;
	}

	public void setMatCodigoMedicamentoEdicao(Integer matCodigoMedicamentoEdicao) {
		this.matCodigoMedicamentoEdicao = matCodigoMedicamentoEdicao;
	}

	public Boolean getPrescricaoAmbulatorial() {
		return prescricaoAmbulatorial;
	}

	public void setPrescricaoAmbulatorial(Boolean prescricaoAmbulatorial) {
		this.prescricaoAmbulatorial = prescricaoAmbulatorial;
	}
}
