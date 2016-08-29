package br.gov.mec.aghu.emergencia.perinatologia.action;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.inject.Inject;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.jfree.chart.ChartUtilities;
import org.jfree.util.Log;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.dominio.DominioTipoParto;
import br.gov.mec.aghu.emergencia.business.IEmergenciaFacade;
import br.gov.mec.aghu.emergencia.vo.DadosBolsaRotasVO;
import br.gov.mec.aghu.emergencia.vo.IndicacaoNascimentoVO;
import br.gov.mec.aghu.emergencia.vo.MedicamentoVO;
import br.gov.mec.aghu.emergencia.vo.TrabalhoPartosVO;
import br.gov.mec.aghu.model.McoAtendTrabPartos;
import br.gov.mec.aghu.model.McoAtendTrabPartosId;
import br.gov.mec.aghu.model.McoBolsaRotas;
import br.gov.mec.aghu.model.McoGestacoes;
import br.gov.mec.aghu.model.McoGestacoesId;
import br.gov.mec.aghu.model.McoIndicacaoNascimento;
import br.gov.mec.aghu.model.McoMedicamentoTrabPartos;
import br.gov.mec.aghu.model.McoMedicamentoTrabPartosId;
import br.gov.mec.aghu.model.McoTrabPartos;
import br.gov.mec.aghu.paciente.prontuarioonline.business.IProntuarioOnlineFacade;
import br.gov.mec.aghu.registrocolaborador.vo.RapServidorConselhoVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.AGHUUtil;
import br.gov.mec.aghu.service.ServiceException;

public class RegistrarGestacaoAbaTrabalhoPartoController extends ActionController {

	private static final String CAMPO_OBRIGATORIO = "CAMPO_OBRIGATORIO";
	private static final String MENSAGEM_TRABALHO_PARTO_SUCESSO_EXCLUSAO_MEDICAMENTO = "MENSAGEM_SUCESSO_EXCLUSAO_MEDICAMENTO";
	private static final String MENSAGEM_SUCESSO_ALTERACAO_TRABALHO_PARTO = "MENSAGEM_TRABALHO_PARTO_ALTERACAO_INCLUSAO_TRABALHO_PARTO";
	private static final String MENSAGEM_SUCESSO_INCLUSAO_TRABALHO_PARTO = "MENSAGEM_TRABALHO_PARTO_SUCESSO_INCLUSAO_TRABALHO_PARTO";
	private static final String MENSAGEM_TRABALHO_PARTO_SUCESSO_INCLUSAO_MEDICAMENTO = "MENSAGEM_TRABALHO_PARTO_SUCESSO_INCLUSAO_MEDICAMENTO";
	private static final String MENSAGEM_SUCESSO_ALTERACAO_MEDICAMENTO = "MENSAGEM_SUCESSO_ALTERACAO_MEDICAMENTO";

	@EJB
	private IEmergenciaFacade emergenciaFacade;
	
	@EJB
	private IProntuarioOnlineFacade prontuarioOnlineFacade;
	
	@EJB
	private IPermissionService permissionService;
	
	@Inject
	private RegistrarGestacaoController registrarGestacaoController;
	
	@Inject
	private RegistrarGestacaoAbaNascimentoController  registrarGestacaoAbaNascimentoController;

	private static final long serialVersionUID = 6589546988357451478L;
	private McoAtendTrabPartos atendTrabParto;
	private McoAtendTrabPartos atendTrabPartoEdicao;
	private DadosBolsaRotasVO dadosBolsaRotasVO;
	private DadosBolsaRotasVO dadosBolsaRotasVOOriginal;
	private boolean exibirModalExcluirMedicamento;
	private McoMedicamentoTrabPartos medicamentoTrabPartos;
	private IndicacaoNascimentoVO indicacao;
	private RapServidorConselhoVO responsavel1;
	private RapServidorConselhoVO responsavel2;
	private IndicacaoNascimentoVO indicacaoOriginal;
	private RapServidorConselhoVO responsavel1Original;
	private RapServidorConselhoVO responsavel2Original;
	private MedicamentoVO medicamentoVO;
	private Date dataHoraInicial;
	private Date dataHoraFinal;
	private String observacao;
	private br.gov.mec.aghu.farmacia.vo.MedicamentoVO medicamentoVO2;
	private br.gov.mec.aghu.farmacia.vo.MedicamentoVO medicamentoSuggestionVO;
	private MedicamentoVO medicamentoSelecionado;
	private TrabalhoPartosVO trabalhoPartosVO;
	private TrabalhoPartosVO trabalhoPartosVOOriginal;
	private List<MedicamentoVO> medicamentos = new ArrayList<>();
	private Integer pacCodigo;
	private Short seqp;
	private List<McoAtendTrabPartos> listaAtendTrabParto;
	private Integer gemelar;
	private boolean edicaoAtendTrabParto;
	private boolean verificarGemelar;
	private Integer numeroConsulta;
	private String voltarPara;
	private boolean abilitarEdicao;
	private boolean mostrarAcoes;
	private boolean permissaoManterTrabalhoParto;
	private boolean permissaoVisualizarTrabalhoParto;
	private boolean campoIndicacaoNascimentoObrigatorio;
	private boolean campoResponsavel1Obrigatorio;
	private boolean mostrarCampoJustificativa;
	private boolean habilitaSumarioPrevio;
	private boolean voltarEmergencia;
	private boolean alteracoesAtendimentoTrabalhoParto;
	private boolean alteracaoItensMedicamento;
	private boolean mostrarModalDadosPendentesAbaTrabParto;
	private boolean mantemAba;
	private boolean imprimirPrevia;	
	
	public void iniciarPermissao() {
		this.setPermissaoManterTrabalhoParto(this.permissionService.usuarioTemPermissao(obterLoginUsuarioLogado(), "manterTrabalhoParto", "gravar"));
		this.setPermissaoVisualizarTrabalhoParto(this.permissionService.usuarioTemPermissao(obterLoginUsuarioLogado(), "visualizarTrabalhoParto", "visualizar"));
		this.setImprimirPrevia(this.permissionService.usuarioTemPermissao(obterLoginUsuarioLogado(), "imprimirSumarioPreviaParto", "visualizar"));
		setEdicaoAtendTrabParto(Boolean.FALSE);
	}

	public Long pesquisarMedicamentoCount(String objPesquisa) {
		try {
			return this.emergenciaFacade.obterMcoMedicamentoPorSeqOuDescricaoCount(objPesquisa);
		} catch (ServiceException e) {
			this.apresentarMsgNegocio("data_atendimento", Severity.ERROR, CAMPO_OBRIGATORIO, "LABEL_CRIACAO_PARTOGRAMA_DATA_ATENDIMENTO");
		}
		return 0l;
	}

	public List<br.gov.mec.aghu.farmacia.vo.MedicamentoVO> pesquisarMedicamento(
			String objPesquisa) {
		try {
			return  this.returnSGWithCount(this.emergenciaFacade.obterMcoMedicamentoPorSeqOuDescricao(objPesquisa),pesquisarMedicamentoCount(objPesquisa));
		} catch (ServiceException e) {
			this.apresentarMsgNegocio("data_atendimento", Severity.ERROR, CAMPO_OBRIGATORIO, "LABEL_CRIACAO_PARTOGRAMA_DATA_ATENDIMENTO");
		}
		return new ArrayList<br.gov.mec.aghu.farmacia.vo.MedicamentoVO>(0);
	}

	public void adicionarMedicamento() {
		try {
			if (medicamentoSuggestionVO != null) {
				McoMedicamentoTrabPartos medicamento = new McoMedicamentoTrabPartos();
				Integer medMatCodigo = medicamentoSuggestionVO.getMatCodigo();
			
				medicamento.setId(new McoMedicamentoTrabPartosId(getPacCodigo(), getSeqp(), medMatCodigo));
				medicamento.setDataHoraInicial(medicamentoVO.getDataHoraInicial());
				medicamento.setDataHoraFinal(medicamentoVO.getDataHoraFinal());
				medicamento.setObservacao(medicamentoVO.getObservacao());

				this.emergenciaFacade.adicionarMedicamento(medicamento);

				listarMedicamentos(getPacCodigo(), getSeqp());

				limparCamposCadastroMedicamento();
				this.apresentarMsgNegocio(Severity.INFO, MENSAGEM_TRABALHO_PARTO_SUCESSO_INCLUSAO_MEDICAMENTO);
				
				alteracaoItensMedicamento = true;
			} else {
				this.apresentarMsgNegocio("codigo_megicamento", Severity.ERROR, CAMPO_OBRIGATORIO, "Medicamento");
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void confirmarEdicaoMedicamento() {
		try {
			McoMedicamentoTrabPartos medicamento = this.emergenciaFacade.buscarMcoMedicamentoTrabPartosPorId(getPacCodigo(), getSeqp(), medicamentoVO.getMatCodigo());
			medicamento.setDataHoraInicial(medicamentoVO.getDataHoraInicial());
			medicamento.setDataHoraFinal(medicamentoVO.getDataHoraFinal());
			medicamento.setObservacao(medicamentoVO.getObservacao());

			this.emergenciaFacade.alterarMcoMedicamentoTrabPartos(medicamento);

			listarMedicamentos(getPacCodigo(), getSeqp());

			limparCamposCadastroMedicamento();
			this.apresentarMsgNegocio(Severity.INFO, MENSAGEM_SUCESSO_ALTERACAO_MEDICAMENTO);
			mostrarBotoesEdicaoMedicamentos(Boolean.FALSE);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void fecharModalPendencias(){
		alteracaoItensMedicamento = false;
		alteracoesAtendimentoTrabalhoParto = false;
		mostrarModalDadosPendentesAbaTrabParto = false;
		dadosBolsaRotasVO = new DadosBolsaRotasVO();
		dadosBolsaRotasVOOriginal = new DadosBolsaRotasVO();
		trabalhoPartosVOOriginal = new TrabalhoPartosVO();
		indicacao = null;
		responsavel1 = null;
		responsavel2 = null;
		indicacaoOriginal = null;
		responsavel1Original = null;
		responsavel2Original = null;
		trabalhoPartosVO = new TrabalhoPartosVO();
	}
	
	public boolean validarAlteracoes(){
		return alteracoesAtendimentoTrabalhoParto || alteracaoItensMedicamento || verificaAlteracaoBolsaRota() || 
				verificaAlteracaoIndicacao() || verificaAlteracaoResponsavel1() || verificaAlteracaoResponsavel2() || 
				verificaAlteracaoTrabalhoParto();
	}
	
	public void gravarTrabalhoParto() {
		try {
			mostrarModalDadosPendentesAbaTrabParto = false;
			if (preGravarTrabalhoParto()) {
				McoGestacoesId id = new McoGestacoesId(getPacCodigo(), getSeqp());
				
				McoBolsaRotas bolsaRotas = this.emergenciaFacade.obterBolsaRotaPorId(getPacCodigo(), getSeqp());
				if (bolsaRotas == null) {
					bolsaRotas = new McoBolsaRotas(id);
				}
		
				McoGestacoes mcoGestacoes = this.emergenciaFacade.pesquisarMcoGestacaoPorPacCodigoSeqp(getPacCodigo(), getSeqp());
				bolsaRotas.setMcoGestacoes(mcoGestacoes);
	
				bolsaRotas.setDominioFormaRuptura(dadosBolsaRotasVO.getFormaRuptura());
				bolsaRotas.setDthrRompimento(dadosBolsaRotasVO.getDataHoraRompimento());
				bolsaRotas.setLiquidoAmniotico(dadosBolsaRotasVO.getLiquidoAmniotico());
				bolsaRotas.setIndOdorFetido(dadosBolsaRotasVO.isIndOdorFetido());
				bolsaRotas.setIndDataHoraIgnorada(dadosBolsaRotasVO.getIndDataHoraIgnorada());
				McoTrabPartos mcoTrabPartos = this.emergenciaFacade.obterMcoTrabPartoPorId(getPacCodigo(), getSeqp());
				boolean operacao = true;
				if (mcoTrabPartos == null ) {
					mcoTrabPartos = new McoTrabPartos();
					operacao = false;
					
				}
				mcoTrabPartos.setId(id);
				mcoTrabPartos.setIndicadorPartoInduzido(trabalhoPartosVO.getIndicadorPartoInduzido());
				mcoTrabPartos.setDthriniCtg(trabalhoPartosVO.getDataHoraInicialCtg());
				mcoTrabPartos.setIndicacoesCtg(StringUtils.isBlank(trabalhoPartosVO.getIndicacoesCtg()) ? null : trabalhoPartosVO.getIndicacoesCtg() );
				mcoTrabPartos.setTipoParto(trabalhoPartosVO.getTipoParto());
				novaInformacaoIndicacaoResponsavel(mcoTrabPartos);
				// TODO SERVIDOR RESPONSÁVEL 1 E 2
				mcoTrabPartos.setObservacao(trabalhoPartosVO.getObservacao());
				mcoTrabPartos.setJustificativa(trabalhoPartosVO.getJustificativa());
	
				this.emergenciaFacade.gravarTrabalhoParto(mcoTrabPartos,	bolsaRotas);
				
				if (operacao) {
					this.apresentarMsgNegocio(Severity.INFO, MENSAGEM_SUCESSO_ALTERACAO_TRABALHO_PARTO);
				}else{
					this.apresentarMsgNegocio(Severity.INFO, MENSAGEM_SUCESSO_INCLUSAO_TRABALHO_PARTO);
				}
				alteracoesAtendimentoTrabalhoParto = false;
				alteracaoItensMedicamento = false;
				mostrarModalDadosPendentesAbaTrabParto = false;
				prepararAbaTrabalhoParto(pacCodigo, seqp, numeroConsulta);		
			}
			else {
				this.setMantemAba(true);
			}
		} catch (ApplicationBusinessException e) {
			this.setMantemAba(true);
			apresentarExcecaoNegocio(e);
		}
	}
	public void gravarTrabalhoPartoSemMsgSucesso() {
		try {
			mostrarModalDadosPendentesAbaTrabParto = false;
			boolean validado =	preGravarTrabalhoParto();
			if (validado) {
				McoGestacoesId id = new McoGestacoesId(getPacCodigo(), getSeqp());
				
				McoBolsaRotas bolsaRotas = this.emergenciaFacade.obterBolsaRotaPorId(getPacCodigo(), getSeqp());
				if (bolsaRotas == null) {
					bolsaRotas = new McoBolsaRotas(id);
				}
		
				McoGestacoes mcoGestacoes = this.emergenciaFacade.pesquisarMcoGestacaoPorPacCodigoSeqp(getPacCodigo(), getSeqp());
				bolsaRotas.setMcoGestacoes(mcoGestacoes);
	
				bolsaRotas.setDominioFormaRuptura(dadosBolsaRotasVO.getFormaRuptura());
				bolsaRotas.setDthrRompimento(dadosBolsaRotasVO.getDataHoraRompimento());
				bolsaRotas.setLiquidoAmniotico(dadosBolsaRotasVO.getLiquidoAmniotico());
				bolsaRotas.setIndOdorFetido(dadosBolsaRotasVO.isIndOdorFetido());
				bolsaRotas.setIndDataHoraIgnorada(dadosBolsaRotasVO.getIndDataHoraIgnorada());
				McoTrabPartos mcoTrabPartos = this.emergenciaFacade.obterMcoTrabPartoPorId(getPacCodigo(), getSeqp());
				if(mcoTrabPartos == null){
					mcoTrabPartos = new McoTrabPartos();
				}
				mcoTrabPartos.setId(id);
				mcoTrabPartos.setIndicadorPartoInduzido(trabalhoPartosVO.getIndicadorPartoInduzido());
				mcoTrabPartos.setDthriniCtg(trabalhoPartosVO.getDataHoraInicialCtg());
				mcoTrabPartos.setIndicacoesCtg(StringUtils.isBlank(trabalhoPartosVO.getIndicacoesCtg()) ? null : trabalhoPartosVO.getIndicacoesCtg() );
				mcoTrabPartos.setTipoParto(trabalhoPartosVO.getTipoParto());
				novaInformacaoIndicacaoResponsavel(mcoTrabPartos);
				// TODO SERVIDOR RESPONSÁVEL 1 E 2
				mcoTrabPartos.setObservacao(trabalhoPartosVO.getObservacao());
				mcoTrabPartos.setJustificativa(trabalhoPartosVO.getJustificativa());
	
				this.emergenciaFacade.gravarTrabalhoParto(mcoTrabPartos,	bolsaRotas);
				
				alteracoesAtendimentoTrabalhoParto = false;
				alteracaoItensMedicamento = false;
				mostrarModalDadosPendentesAbaTrabParto = false;
				prepararAbaTrabalhoParto(pacCodigo, seqp, numeroConsulta);		
			}
			else {
				this.setMantemAba(true);
			}
		} catch (ApplicationBusinessException e) {
			this.setMantemAba(true);
			apresentarExcecaoNegocio(e);
		}
	}
	private void novaInformacaoIndicacaoResponsavel(McoTrabPartos mcoTrabPartos) {
		if (indicacao != null && indicacao.getSeq() != null) {
			mcoTrabPartos.setIndicacaoNascimento(this.emergenciaFacade.obterMcoIndicacaoNascimentoPorChavePrimaria(indicacao.getSeq()));
		}
		if (indicacao == null && indicacaoOriginal != null) {
			mcoTrabPartos.setIndicacaoNascimento(null);
		}
		if (responsavel1 != null) {
			mcoTrabPartos.setSerMatriculaIndicado(responsavel1.getMatricula());
			mcoTrabPartos.setSerVinCodigoIndicado(responsavel1.getVinCodigo());
		}
		if(responsavel1 == null && responsavel1Original != null) {
			mcoTrabPartos.setSerMatriculaIndicado(null);
			mcoTrabPartos.setSerVinCodigoIndicado(null);
		}
		if (responsavel2 != null) {
			mcoTrabPartos.setSerMatriculaIndicado2(responsavel2.getMatricula());
			mcoTrabPartos.setSerVinCodigoIndicado2(responsavel2.getVinCodigo());
		}
		if(responsavel2 == null && responsavel2Original != null) {
			mcoTrabPartos.setSerMatriculaIndicado2(null);
			mcoTrabPartos.setSerVinCodigoIndicado2(null);
		}
	}

	private boolean preGravarTrabalhoParto() {
		if (trabalhoPartosVO != null){
			if(StringUtils.isBlank(trabalhoPartosVO.getJustificativa()) && mostrarCampoJustificativa) {
					this.apresentarMsgNegocio("inputTrabalhoPartoJustNaoPreenchimento", Severity.ERROR, CAMPO_OBRIGATORIO, "Justificativa");
					return false;
			}
			if(trabalhoPartosVO.getIndicadorPartoInduzido() == null){
				this.apresentarMsgNegocio("indPartoInduzido", Severity.ERROR, CAMPO_OBRIGATORIO, "Parto Induzido");
				return false;
			}
			if (campoIndicacaoNascimentoObrigatorio && indicacao == null) {
				this.apresentarMsgNegocio("inputTrabalhoPartoIndicacao", Severity.ERROR, CAMPO_OBRIGATORIO, "Indicação");
				return false;
			}
			if (campoResponsavel1Obrigatorio && responsavel1== null) {
				this.apresentarMsgNegocio("inputTrabalhoPartoIndicacao", Severity.ERROR, CAMPO_OBRIGATORIO, "Responsável 1");
				return false;
			}
			
		}
		return true;
	}

	private void listarMedicamentos(Integer gsoPacCodigo, Short gsoSeqp) {
		this.medicamentos = this.emergenciaFacade.pesquisarTrabPartoMedicamentos(gsoPacCodigo, gsoSeqp);
	}

	public void cancelarEdicaoMedicamento() {
		limparCamposCadastroMedicamento();
		mostrarBotoesEdicaoMedicamentos(Boolean.FALSE);
		listarMedicamentos(getPacCodigo(), getSeqp());
		alteracaoItensMedicamento = false;
	}

	public void limparCamposCadastroMedicamento() {
		this.medicamentoSuggestionVO = null;
		this.medicamentoVO = null;
		this.medicamentoVO = new MedicamentoVO();
		this.medicamentoSelecionado = null;
	}

	private void mostrarBotoesEdicaoMedicamentos(Boolean mostrar) {
		this.mostrarAcoes = !(this.abilitarEdicao = mostrar);
	}

	public void selecionarMedicamento(MedicamentoVO medicamento) {

		this.medicamentoSuggestionVO.setMatCodigo(medicamento.getMatCodigo());
		this.medicamentoVO.setDataHoraInicial(medicamento.getDataHoraInicial());
		this.medicamentoVO.setDataHoraFinal(medicamento.getDataHoraFinal());
		this.medicamentoVO.setDescricao(medicamento.getDescricao());

		mostrarBotoesEdicaoMedicamentos(Boolean.TRUE);
	}

	public void excluirMedicamento() {
		Integer matCodigo = medicamentoVO.getMatCodigo();
		Short gsoSeqp = getSeqp();
		Integer gsoPacCodigo = getPacCodigo();
		this.emergenciaFacade.excluirMedicamento(matCodigo, gsoSeqp, gsoPacCodigo);
		listarMedicamentos(getPacCodigo(), getSeqp());
		this.apresentarMsgNegocio(Severity.INFO, MENSAGEM_TRABALHO_PARTO_SUCESSO_EXCLUSAO_MEDICAMENTO);
		alteracaoItensMedicamento = true;
	}

	public void editarMedicamento() {
		this.medicamentoSuggestionVO = new br.gov.mec.aghu.farmacia.vo.MedicamentoVO();
		this.medicamentoSuggestionVO.setMatCodigo(this.medicamentoVO.getMatCodigo());
		this.medicamentoSuggestionVO.setDescricao(this.medicamentoVO.getDescricao());
		mostrarBotoesEdicaoMedicamentos(Boolean.TRUE);
	}

	public void abrirModalExcluirMedicamento(MedicamentoVO medicamentoVO) {
		this.exibirModalExcluirMedicamento = true;
	}

	public void limparCamposTrabahoDeParto() {
		this.trabalhoPartosVO.setIndicadorPartoInduzido(null);
		this.trabalhoPartosVO.setDataHoraInicialCtg(null);
		this.trabalhoPartosVO.setIndicacoesCtg(null);
		this.trabalhoPartosVO.setTipoParto(null);
		this.trabalhoPartosVO.setObservacao(null);
		this.trabalhoPartosVO.setJustificativa(null);
		this.indicacao = null;
		responsavel1 = null;
		responsavel2 = null;
		atualizarDecisaoTomada();
	}

	public void atualizarDecisaoTomada() {
		DominioTipoParto tipoParto = trabalhoPartosVO.getTipoParto();
		if(tipoParto!=null){
			List<McoAtendTrabPartos> mcoTrabPartos = emergenciaFacade.buscarListaMcoAtendTrabPartos(getPacCodigo(), getSeqp());
			if(mcoTrabPartos == null || mcoTrabPartos.size() == 0){
				this.mostrarCampoJustificativa = true;
			} 
			
			if(tipoParto.equals(DominioTipoParto.C)){
				this.campoIndicacaoNascimentoObrigatorio =  true;
				this.campoResponsavel1Obrigatorio =  true;
			}else{
				this.campoIndicacaoNascimentoObrigatorio =  false;
				this.campoResponsavel1Obrigatorio =  false;
			}
		} else{
			this.campoIndicacaoNascimentoObrigatorio =  false;
			this.campoResponsavel1Obrigatorio =  false;
			this.mostrarCampoJustificativa = false;
		}
	}

	public void inicioPartograma() {
		setEdicaoAtendTrabParto(Boolean.FALSE);
		atendTrabParto = new McoAtendTrabPartos();
		montarListaAtendParto();
		McoGestacoes gestacao = this.emergenciaFacade.pesquisarMcoGestacaoPorPacCodigoSeqp(getPacCodigo(), getSeqp());
		if (gestacao != null) {
			if (CoreUtil.isNumeroInteger(gestacao.getGemelar())) {
				setGemelar(Integer.valueOf(gestacao.getGemelar()));
			} else {
				setGemelar(0);
			}
		}
		
		verificarSumarioPrevio();
	}

	public void inicioMedicamentos() {
		this.medicamentoVO = new MedicamentoVO();
		this.medicamentoVO2 = new br.gov.mec.aghu.farmacia.vo.MedicamentoVO();
		listarMedicamentos(getPacCodigo(), getSeqp());
	}

	public void prepararAbaTrabalhoParto(Integer pacCodigo, Short seqp, Integer numeroConsulta) {
		this.pacCodigo = pacCodigo;
		this.seqp = seqp;
		this.numeroConsulta = numeroConsulta;

		mostrarBotoesEdicaoMedicamentos(Boolean.FALSE);
		mantemAba = false;

		iniciarPermissao();
		inicioPartograma();
		inicioBolsaRotas();
		inicioTrabalhoParto();
		inicioMedicamentos();
		atualizarDecisaoTomada();

	}

	private void verificarSumarioPrevio(){
		habilitaSumarioPrevio = this.emergenciaFacade.verificarSumarioPrevio(getPacCodigo(), getSeqp());
		this.setVoltarPara("/emergencia/pages/perinatologia/registrarGestacao.xhtml");
		this.setVoltarEmergencia(Boolean.TRUE);
	}
	
	public void gravaDadosPendentes(){
		gravarTrabalhoParto();
		if(!mantemAba){ //caso queira salvar dados pendente e ocorrer erro, mantem na aba atual
			this.fecharModalPendencias();
			registrarGestacaoController.preparaAbaDestino();
		}
	}
	
	public void ignoraDadosPendencias(){
		this.fecharModalPendencias();
		mantemAba = false;
		registrarGestacaoController.preparaAbaDestino();
	}

	private void inicioTrabalhoParto() {
		mantemAba = false;
		McoTrabPartos mcoTrabPartos = this.emergenciaFacade.buscarMcoTrabPartos(getPacCodigo(), getSeqp());
 		this.trabalhoPartosVO = new TrabalhoPartosVO();
		if (mcoTrabPartos != null) {
			this.trabalhoPartosVO.setIndicadorPartoInduzido(mcoTrabPartos.getIndicadorPartoInduzido() != null ? mcoTrabPartos.getIndicadorPartoInduzido().booleanValue() : false);
			this.trabalhoPartosVO.setDataHoraInicialCtg(mcoTrabPartos.getDthriniCtg());
			this.trabalhoPartosVO.setIndicacoesCtg(mcoTrabPartos.getIndicacoesCtg());
			this.trabalhoPartosVO.setTipoParto(mcoTrabPartos.getTipoParto());
			this.trabalhoPartosVO.setObservacao(mcoTrabPartos.getObservacao());
			this.trabalhoPartosVO.setJustificativa(mcoTrabPartos.getJustificativa());
			if (mcoTrabPartos.getIndicacaoNascimento() != null) {
				this.indicacao = new IndicacaoNascimentoVO();
				this.indicacao.setSeq(mcoTrabPartos.getIndicacaoNascimento().getSeq());
				this.indicacao.setDescricao(mcoTrabPartos.getIndicacaoNascimento().getDescricao());
			} 
			
			if (mcoTrabPartos.getSerMatriculaIndicado() != null && mcoTrabPartos.getSerVinCodigoIndicado() !=null) {
				RapServidorConselhoVO vo = buscarPessoaFisicaConselho(mcoTrabPartos.getSerVinCodigoIndicado(), mcoTrabPartos.getSerMatriculaIndicado());
				if (vo != null) {
					this.responsavel1 = new RapServidorConselhoVO();
					this.responsavel1.setNome(vo.getNome());
					this.responsavel1.setMatricula(vo.getMatricula());
					this.responsavel1.setVinCodigo(vo.getVinCodigo());
					this.responsavel1.setNroRegConselho(vo.getNroRegConselho());
					this.responsavel1.setSigla(vo.getSigla());
					this.responsavel1.setDtFimVinculo(vo.getDtFimVinculo());
					this.responsavel1.setDtInicioVinculo(vo.getDtInicioVinculo());
					this.responsavel1.setSituacao(vo.getSituacao());
				}		
			}
			if (mcoTrabPartos.getSerMatriculaIndicado2() != null && mcoTrabPartos.getSerVinCodigoIndicado2() !=null) {
				RapServidorConselhoVO vo = buscarPessoaFisicaConselho(mcoTrabPartos.getSerVinCodigoIndicado2(), mcoTrabPartos.getSerMatriculaIndicado2());
				if (vo != null) {
					this.responsavel2 = new RapServidorConselhoVO();
					this.responsavel2.setNome(vo.getNome());
					this.responsavel2.setMatricula(vo.getMatricula());
					this.responsavel2.setVinCodigo(vo.getVinCodigo());
					this.responsavel2.setNroRegConselho(vo.getNroRegConselho());
					this.responsavel2.setSigla(vo.getSigla());
					this.responsavel2.setDtFimVinculo(vo.getDtFimVinculo());
					this.responsavel2.setDtInicioVinculo(vo.getDtInicioVinculo());
					this.responsavel2.setSituacao(vo.getSituacao());
				}
			}	
		}
		else { // quando nao possuir informacoes de trabalho de parto 
			indicacao = null;
			responsavel1 = null;
			responsavel2 = null;
		}
	
		
		salvarDadosOriginais();
		
	}

	private void salvarDadosOriginais() {
		try {
			if(trabalhoPartosVO != null){
				trabalhoPartosVOOriginal = new TrabalhoPartosVO();
				PropertyUtils.copyProperties(trabalhoPartosVOOriginal, trabalhoPartosVO);
			}
			if(indicacao != null) {
				indicacaoOriginal = new IndicacaoNascimentoVO();
				PropertyUtils.copyProperties(indicacaoOriginal, indicacao);
			}
			if(responsavel1 != null) {
				responsavel1Original = new RapServidorConselhoVO();
				PropertyUtils.copyProperties(responsavel1Original, responsavel1);
			}
			if(responsavel2 != null) {
				responsavel2Original = new RapServidorConselhoVO();
				PropertyUtils.copyProperties(responsavel2Original, responsavel2);
			}
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			Log.error("Erro ao copiar objetos.", e);
		}
	}
	public boolean verificaAlteracaoTrabalhoParto(){
		if(trabalhoPartosVO != null){
			return AGHUUtil.modificados(trabalhoPartosVO.getIndicadorPartoInduzido(), trabalhoPartosVOOriginal.getIndicadorPartoInduzido()) ||
					AGHUUtil.modificados(trabalhoPartosVO.getDataHoraInicialCtg(), trabalhoPartosVOOriginal.getDataHoraInicialCtg()) || 
					AGHUUtil.modificados(trabalhoPartosVO.getIndicacoesCtg(), trabalhoPartosVOOriginal.getIndicacoesCtg()) ||
					AGHUUtil.modificados(trabalhoPartosVO.getTipoParto(), trabalhoPartosVOOriginal.getTipoParto()) || 
					AGHUUtil.modificados(trabalhoPartosVO.getObservacao(), trabalhoPartosVOOriginal.getObservacao());
		}
		else {
			return false;
		}
	}
	public boolean verificaAlteracaoIndicacao(){
		if(indicacao != null && indicacaoOriginal == null){
			return true;
		}
		else if(indicacao == null && indicacaoOriginal != null){
			return true;
		}
		else if (indicacao == null && indicacaoOriginal == null){
			return false;
		}
		return AGHUUtil.modificados(indicacao.getSeq(), indicacaoOriginal.getSeq());
	}
	public boolean verificaAlteracaoResponsavel1(){
		if(responsavel1 != null && responsavel1Original == null){
			return true;
		}
		else if(responsavel1 == null && responsavel1Original != null){
			return true;
		}
		else if(responsavel1 == null && responsavel1Original == null){
			return false;
		}
		return AGHUUtil.modificados(responsavel1.getMatricula(), responsavel1Original.getMatricula());
	}
	public boolean verificaAlteracaoResponsavel2(){
		if(responsavel2 != null && responsavel2Original == null){
			return true;
		}
		else if(responsavel2 == null && responsavel2Original != null){
			return true;
		}
		else if(responsavel2 == null && responsavel2Original == null){
			return false;
		}
		return AGHUUtil.modificados(responsavel2.getMatricula(), responsavel2Original.getMatricula());
	}
	private RapServidorConselhoVO buscarPessoaFisicaConselho(Short vinculo, Integer matricula){
		RapServidorConselhoVO vo = null;
		try {
			vo = this.emergenciaFacade.obterRapPessoasConselhoPorMatriculaVinculo(vinculo, matricula);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return vo;
	}
	private void inicioBolsaRotas() {
		McoBolsaRotas bolsaRotas = this.emergenciaFacade.buscarBolsaRotas(getPacCodigo(), getSeqp());
		this.dadosBolsaRotasVO = new DadosBolsaRotasVO();
		
		if (bolsaRotas != null) {
			this.dadosBolsaRotasVO.setDataHoraRompimento(bolsaRotas.getDthrRompimento());
			this.dadosBolsaRotasVO.setFormaRuptura(bolsaRotas.getDominioFormaRuptura());
			this.dadosBolsaRotasVO.setIndDataHoraIgnorada(bolsaRotas.getIndDataHoraIgnorada());
			this.dadosBolsaRotasVO.setLiquidoAmniotico(bolsaRotas.getLiquidoAmniotico());
			if (bolsaRotas.getIndOdorFetido() != null) {
				this.dadosBolsaRotasVO.setIndOdorFetido(bolsaRotas.getIndOdorFetido().booleanValue());
			}
		}
		try {
			dadosBolsaRotasVOOriginal = new DadosBolsaRotasVO();
			PropertyUtils.copyProperties(dadosBolsaRotasVOOriginal, dadosBolsaRotasVO);
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			Log.error("erro ao copiar dados da bolsa Vo", e);
		}
	}
	public boolean verificaAlteracaoBolsaRota(){
		if(dadosBolsaRotasVO != null){
			return AGHUUtil.modificados(dadosBolsaRotasVOOriginal.getDataHoraRompimento(), dadosBolsaRotasVO.getDataHoraRompimento()) || 
					AGHUUtil.modificados(dadosBolsaRotasVOOriginal.getFormaRuptura(), dadosBolsaRotasVO.getFormaRuptura()) || 
					AGHUUtil.modificados(dadosBolsaRotasVOOriginal.getIndDataHoraIgnorada(), dadosBolsaRotasVO.getIndDataHoraIgnorada()) ||
					AGHUUtil.modificados(dadosBolsaRotasVOOriginal.getLiquidoAmniotico(), dadosBolsaRotasVO.getLiquidoAmniotico()) ||
					AGHUUtil.modificados(dadosBolsaRotasVOOriginal.isIndOdorFetido(), dadosBolsaRotasVO.isIndOdorFetido());
		}
		else {
			return false;
		}
	}
	public void montarListaAtendParto() {
		setListaAtendTrabParto(this.emergenciaFacade.listarAtendPartosPorId(
				getSeqp(), getPacCodigo()));
	}
	private boolean preGravarAtendTrabParto() {
		if (getAtendTrabParto().getDthrAtend() == null) {
			this.apresentarMsgNegocio("data_atendimento", Severity.ERROR, CAMPO_OBRIGATORIO, "Data de Atendimento");
			return false;
		}
		if (getAtendTrabParto().getId() != null) {
			getAtendTrabParto().getId().setGsoPacCodigo(getPacCodigo());
			getAtendTrabParto().getId().setGsoSeqp(getSeqp());
		} else {
			getAtendTrabParto().setId(
					new McoAtendTrabPartosId(getPacCodigo(), getSeqp(), null));
		}
		return true;
	}
	public void gravarAtendTrabParto() {
		try {
			if (getAtendTrabParto() != null) {
				if (preGravarAtendTrabParto()) {
					this.emergenciaFacade.gravarAtendTrabParto(getAtendTrabParto());
					if (isEdicaoAtendTrabParto()) {
						apresentarMsgNegocio(Severity.INFO, "MENSAGEM_CRIACAO_PARTOGRAMA_SUCESSO_ALTERACAO_ATEND_TRAB_PARTO");
					} else {
						apresentarMsgNegocio(Severity.INFO, "MENSAGEM_CRIACAO_PARTOGRAMA_SUCESSO_ADICAO_ATEND_TRAB_PARTO");
					}
					alteracoesAtendimentoTrabalhoParto = true;
				}
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return;
		}
		inicioPartograma();
	}
	public void excluirAtendTrabParto() {
		this.emergenciaFacade.excluirAtendTrabParto(getAtendTrabParto());
		apresentarMsgNegocio(Severity.INFO, "MENSAGEM_CRIACAO_PARTOGRAMA_SUCESSO_EXCLUSAO_ATEND_TRAB_PARTO");
		inicioPartograma();
		alteracoesAtendimentoTrabalhoParto = true;
	}
	public void editarAtendTrabParto() {
		setEdicaoAtendTrabParto(Boolean.TRUE);
		this.atendTrabParto = new McoAtendTrabPartos();
		try {
			PropertyUtils.copyProperties(this.atendTrabParto, this.atendTrabPartoEdicao);
		} catch (IllegalAccessException | InvocationTargetException
				| NoSuchMethodException e) {
			Log.info(e.getMessage());
		}
	}
	public void cancelarEdicaoAtendTrabParto() {
		setAtendTrabParto(null);
		setAtendTrabPartoEdicao(null);
		setEdicaoAtendTrabParto(Boolean.FALSE);
		alteracoesAtendimentoTrabalhoParto = false;
	}
	public String montarLabelAltBcf(Boolean indicadorTaquicardia, Boolean variabilidadeBatidaMenorQueDez, Boolean semAceleracaoTransitoria){
		LinkedList<String> lista = new LinkedList<String>();
		StringBuffer resultado = new StringBuffer(100);
		resultado.append("");
		if (indicadorTaquicardia) {
			lista.add("Taq");
		}
		if (variabilidadeBatidaMenorQueDez) {
			lista.add("VBB < 10");
		}
		if (semAceleracaoTransitoria) {
			lista.add("Sem AT");
		}
		if (lista.size()>=1) {
			int tamanhoTotal = lista.size();
			int count = 1;
			for (String valor : lista) {
				if (count != tamanhoTotal) {
					resultado.append(valor).append("<br/>");
				}else{
					resultado.append(valor);
				}
				count++;
			}
		}
		return resultado.toString();
	}
	public Long pesquisarIndicacoesNascimentoCount(String objPesquisa) {
		return this.emergenciaFacade.pesquisarIndicacoesNascimentoPorSeqNomeCount((String) objPesquisa);
	}
	public List<IndicacaoNascimentoVO> pesquisarIndicacoesNascimento(String objPesquisa) {
		List<McoIndicacaoNascimento> indicacoes = this.emergenciaFacade.pesquisarIndicacoesNascimentoPorSeqNome((String) objPesquisa);
		List<IndicacaoNascimentoVO> result = new ArrayList<IndicacaoNascimentoVO>(0);
		for (McoIndicacaoNascimento mcoIndicacao : indicacoes) {
			result.add(new IndicacaoNascimentoVO(mcoIndicacao.getSeq(),	mcoIndicacao.getDescricao()));
		}
		return  this.returnSGWithCount(result,pesquisarIndicacoesNascimentoCount(objPesquisa));
	}
	public List<RapServidorConselhoVO> pesquisarResponsavel(String objPesquisa) {
		try {
			return  this.returnSGWithCount(this.emergenciaFacade.pesquisarServidoresConselho((String) objPesquisa),pesquisarResponsavelCount(objPesquisa));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}
	public Long pesquisarResponsavelCount(String objPesquisa) {
		try {
			return this.emergenciaFacade.pesquisarServidoresConselhoCount((String) objPesquisa);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	public long getTimestamp() {
		return System.currentTimeMillis();
	}
	
	public StreamedContent getImagemPrt() throws IOException {
		FacesContext context = FacesContext.getCurrentInstance();
		if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
		    return new DefaultStreamedContent();
		} else {
		    try {
		    	BufferedImage partograma = prontuarioOnlineFacade.getGraficoPartogramaSumAssistParto(
		    			Integer.valueOf(context.getExternalContext().getRequestParameterMap().get("pacCodigo")), 
		    			Short.valueOf(context.getExternalContext().getRequestParameterMap().get("seqp")));
			    	return new DefaultStreamedContent(new ByteArrayInputStream(ChartUtilities.encodeAsPNG(partograma)));
			} catch (ApplicationBusinessException e) {
				this.apresentarExcecaoNegocio(e);
			} catch (IOException e) {
				this.apresentarMsgNegocio(Severity.ERROR, "Erro ao gerar gráficos: " + e.getMessage());
			}
		    return new DefaultStreamedContent();
		}
	}

	public StreamedContent getImagemFrq() throws IOException {
		FacesContext context = FacesContext.getCurrentInstance();
		if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
		    return new DefaultStreamedContent();
		} else {
		    try {
		    	BufferedImage frequenciaCardiaca  = prontuarioOnlineFacade.getGraficoFrequenciaCardiacaFetalSumAssistParto(
		    			Integer.valueOf(context.getExternalContext().getRequestParameterMap().get("pacCodigo")), 
		    			Short.valueOf(context.getExternalContext().getRequestParameterMap().get("seqp")));
		    	return new DefaultStreamedContent(new ByteArrayInputStream(ChartUtilities.encodeAsPNG(frequenciaCardiaca)));
			} catch (ApplicationBusinessException e) {
				this.apresentarExcecaoNegocio(e);
			} catch (IOException e) {
				this.apresentarMsgNegocio(Severity.ERROR, "Erro ao gerar gráficos: " + e.getMessage());
			}
		    return new DefaultStreamedContent();
		}
	}
	
	public DadosBolsaRotasVO getDadosBolsaRotasVO() {
		return dadosBolsaRotasVO;
	}

	public boolean isExibirModalExcluirMedicamento() {
		return exibirModalExcluirMedicamento;
	}

	public void setExibirModalExcluirMedicamento(
			boolean exibirModalExcluirMedicamento) {
		this.exibirModalExcluirMedicamento = exibirModalExcluirMedicamento;
	}

	public void setDadosBolsaRotasVO(DadosBolsaRotasVO dadosBolsaRotasVO) {
		this.dadosBolsaRotasVO = dadosBolsaRotasVO;
	}

	public McoAtendTrabPartos getAtendTrabParto() {
		return atendTrabParto;
	}

	public void setAtendTrabParto(McoAtendTrabPartos atendTrabParto) {
		this.atendTrabParto = atendTrabParto;
	}
	
	public McoAtendTrabPartos getAtendTrabPartoEdicao() {
		return atendTrabPartoEdicao;
	}
	
	public void setAtendTrabPartoEdicao(McoAtendTrabPartos atendTrabPartoEdicao) {
		this.atendTrabPartoEdicao = atendTrabPartoEdicao;
	}

	public List<br.gov.mec.aghu.emergencia.vo.MedicamentoVO> getMedicamentos() {
		return medicamentos;
	}

	public void setMedicamentos(
			List<br.gov.mec.aghu.emergencia.vo.MedicamentoVO> medicamentos) {
		this.medicamentos = medicamentos;
	}

	public MedicamentoVO getMedicamentoVO() {
		return medicamentoVO;
	}

	public void setMedicamentoVO(MedicamentoVO medicamentoVO) {
		this.medicamentoVO = medicamentoVO;
	}

	public McoMedicamentoTrabPartos getMedicamentoTrabPartos() {
		return medicamentoTrabPartos;
	}

	public void setMedicamentoTrabPartos(
			McoMedicamentoTrabPartos medicamentoTrabPartos) {
		this.medicamentoTrabPartos = medicamentoTrabPartos;
	}

	public IndicacaoNascimentoVO getIndicacao() {
		return indicacao;
	}

	public void setIndicacao(IndicacaoNascimentoVO indicacao) {
		this.indicacao = indicacao;
	}

	public br.gov.mec.aghu.emergencia.vo.MedicamentoVO getMedicamentoSelecionado() {
		return medicamentoSelecionado;
	}

	public void setMedicamentoSelecionado(
			br.gov.mec.aghu.emergencia.vo.MedicamentoVO medicamentoSelecionado) {
		this.medicamentoSelecionado = medicamentoSelecionado;
	}

	public List<McoAtendTrabPartos> getListaAtendTrabParto() {
		return listaAtendTrabParto;
	}

	public void setListaAtendTrabParto(
			List<McoAtendTrabPartos> listaAtendTrabParto) {
		this.listaAtendTrabParto = listaAtendTrabParto;
	}

	public Integer getGemelar() {
		return gemelar;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public void setGemelar(Integer gemelar) {
		this.gemelar = gemelar;
	}

	public boolean isEdicaoAtendTrabParto() {
		return edicaoAtendTrabParto;
	}

	public void setEdicaoAtendTrabParto(boolean edicaoAtendTrabParto) {
		this.edicaoAtendTrabParto = edicaoAtendTrabParto;
	}

	public boolean isVerificarGemelar() {
		return verificarGemelar;
	}

	public void setVerificarGemelar(boolean verificarGemelar) {
		this.verificarGemelar = verificarGemelar;
	}

	public Short getSeqp() {
		return seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	public boolean isHabilitarEdicao() {
		return abilitarEdicao;
	}

	public void setHabilitarEdicao(boolean habilitarEdicao) {
		this.abilitarEdicao = habilitarEdicao;
	}

	public boolean isMostrarAcoes() {	return mostrarAcoes;}
	public void setMostrarAcoes(boolean mostrarAcoes) {this.mostrarAcoes = mostrarAcoes;}
	public boolean isPermissaoManterTrabalhoParto() {	return permissaoManterTrabalhoParto;}
	public void setPermissaoManterTrabalhoParto(boolean permissaoManterTrabalhoParto) {	this.permissaoManterTrabalhoParto = permissaoManterTrabalhoParto;	}
	public boolean isPermissaoVisualizarTrabalhoParto() { return permissaoVisualizarTrabalhoParto; }
	public void setPermissaoVisualizarTrabalhoParto(boolean permissaoVisualizarTrabalhoParto) { this.permissaoVisualizarTrabalhoParto = permissaoVisualizarTrabalhoParto; }
	public br.gov.mec.aghu.farmacia.vo.MedicamentoVO getMedicamentoSuggestionVO() {	return medicamentoSuggestionVO;}
	public void setMedicamentoSuggestionVO(			br.gov.mec.aghu.farmacia.vo.MedicamentoVO medicamentoSuggestionVO) {	this.medicamentoSuggestionVO = medicamentoSuggestionVO;	}
	public TrabalhoPartosVO getTrabalhoPartosVO() {	return trabalhoPartosVO;	}
	public void setTrabalhoPartosVO(TrabalhoPartosVO trabalhoPartosVO) {	this.trabalhoPartosVO = trabalhoPartosVO;	}
	public RapServidorConselhoVO getResponsavel1() {	return responsavel1;	}
	public void setResponsavel1(RapServidorConselhoVO responsavel1) {	this.responsavel1 = responsavel1;	}
	public RapServidorConselhoVO getResponsavel2() {	return responsavel2;	}
	public void setResponsavel2(RapServidorConselhoVO responsavel2) {this.responsavel2 = responsavel2;	}
	public br.gov.mec.aghu.farmacia.vo.MedicamentoVO getMedicamentoVO2() {return medicamentoVO2;}
	public void setMedicamentoVO2(br.gov.mec.aghu.farmacia.vo.MedicamentoVO medicamentoVO2) {	this.medicamentoVO2 = medicamentoVO2;}
	public Date getDataHoraInicial() {	return dataHoraInicial;	}
	public void setDataHoraInicial(Date dataHoraInicial) {	this.dataHoraInicial = dataHoraInicial;	}
	public Date getDataHoraFinal() {	return dataHoraFinal;}
	public void setDataHoraFinal(Date dataHoraFinal) {this.dataHoraFinal = dataHoraFinal;}
	public String getObservacao() {return observacao;}
	public void setObservacao(String observacao) {this.observacao = observacao;}
	public boolean isCampoIndicacaoNascimentoObrigatorio() {return campoIndicacaoNascimentoObrigatorio;}
	public void setCampoIndicacaoNascimentoObrigatorio(boolean campoIndicacaoNascimentoObrigatorio) {this.campoIndicacaoNascimentoObrigatorio = campoIndicacaoNascimentoObrigatorio;}
	public boolean isCampoResponsavel1Obrigatorio() {return campoResponsavel1Obrigatorio;}
	public void setCampoResponsavel1Obrigatorio(boolean campoResponsavel1Obrigatorio) {this.campoResponsavel1Obrigatorio = campoResponsavel1Obrigatorio;}
	public boolean isMostrarCampoJustificativa() {return mostrarCampoJustificativa;}
	public void setMostrarCampoJustificativa(boolean mostrarCampoJustificativa) {this.mostrarCampoJustificativa = mostrarCampoJustificativa;}
	public boolean isAbilitarEdicao() {return abilitarEdicao;}
	public void setAbilitarEdicao(boolean abilitarEdicao) {this.abilitarEdicao = abilitarEdicao;}
	public boolean isHabilitaSumarioPrevio() {return habilitaSumarioPrevio;}
	public void setHabilitaSumarioPrevio(boolean habilitaSumarioPrevio) {this.habilitaSumarioPrevio = habilitaSumarioPrevio;}
	public Integer getNumeroConsulta() {return numeroConsulta;}
	public void setNumeroConsulta(Integer numeroConsulta) {this.numeroConsulta = numeroConsulta;}
	public String getVoltarPara() {return voltarPara;}
	public void setVoltarPara(String voltarPara) {this.voltarPara = voltarPara;}
	public boolean isVoltarEmergencia() {return voltarEmergencia;}
	public void setVoltarEmergencia(boolean voltarEmergencia) {this.voltarEmergencia = voltarEmergencia;}
	public boolean isAlteracoesAtendimentoTrabalhoParto() {return alteracoesAtendimentoTrabalhoParto;}
	public void setAlteracoesAtendimentoTrabalhoParto(boolean alteracoesAtendimentoTrabalhoParto) {this.alteracoesAtendimentoTrabalhoParto = alteracoesAtendimentoTrabalhoParto;}
	public boolean isAlteracaoItensMedicamento() {return alteracaoItensMedicamento;}
	public void setAlteracaoItensMedicamento(boolean alteracaoItensMedicamento) {this.alteracaoItensMedicamento = alteracaoItensMedicamento;}
	public boolean isMostrarModalDadosPendentesAbaTrabParto() {return mostrarModalDadosPendentesAbaTrabParto;}
	public void setMostrarModalDadosPendentesAbaTrabParto(boolean mostrarModalDadosPendentesAbaTrabParto) {this.mostrarModalDadosPendentesAbaTrabParto = mostrarModalDadosPendentesAbaTrabParto;}
	public DadosBolsaRotasVO getDadosBolsaRotasVOOriginal() {return dadosBolsaRotasVOOriginal;}
	public void setDadosBolsaRotasVOOriginal(DadosBolsaRotasVO dadosBolsaRotasVOOriginal) {this.dadosBolsaRotasVOOriginal = dadosBolsaRotasVOOriginal;}
	public IndicacaoNascimentoVO getIndicacaoOriginal() {return indicacaoOriginal;}
	public void setIndicacaoOriginal(IndicacaoNascimentoVO indicacaoOriginal) {this.indicacaoOriginal = indicacaoOriginal;}
	public RapServidorConselhoVO getResponsavel1Original() {return responsavel1Original;}
	public void setResponsavel1Original(RapServidorConselhoVO responsavel1Original) {this.responsavel1Original = responsavel1Original;}
	public RapServidorConselhoVO getResponsavel2Original() {return responsavel2Original;}
	public void setResponsavel2Original(RapServidorConselhoVO responsavel2Original) {this.responsavel2Original = responsavel2Original;}
	public TrabalhoPartosVO getTrabalhoPartosVOOriginal() {return trabalhoPartosVOOriginal;}
	public void setTrabalhoPartosVOOriginal(TrabalhoPartosVO trabalhoPartosVOOriginal) {this.trabalhoPartosVOOriginal = trabalhoPartosVOOriginal;}
	public boolean isMantemAba() {return mantemAba;}
	public void setMantemAba(boolean mantemAba) {this.mantemAba = mantemAba;}
	public Boolean getDesabilitaCampos(){return this.registrarGestacaoAbaNascimentoController.getDesabilitarCampos();}
	public boolean isImprimirPrevia() {return imprimirPrevia;}
	public void setImprimirPrevia(boolean imprimirPrevia) {this.imprimirPrevia = imprimirPrevia;}
}