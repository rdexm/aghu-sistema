package br.gov.mec.aghu.prescricaomedica.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoPedido;
import br.gov.mec.aghu.faturamento.cadastrosapoio.business.IFaturamentoApoioFacade;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AacUnidFuncionalSalas;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghProfEspecialidades;
import br.gov.mec.aghu.model.AghProfEspecialidadesId;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.MpmAltaItemPedidoExame;
import br.gov.mec.aghu.model.MpmAltaPedidoExame;
import br.gov.mec.aghu.model.MpmAltaReinternacao;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmMotivoReinternacao;
import br.gov.mec.aghu.model.VAacSiglaUnfSala;
import br.gov.mec.aghu.model.VAelExamesSolicitacao;
import br.gov.mec.aghu.model.VAelExamesSolicitacaoId;
import br.gov.mec.aghu.model.VAinConvenioPlano;
import br.gov.mec.aghu.model.VRapServidorConselho;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;



@SuppressWarnings("PMD.AghuTooManyMethods")
public class ManterSumarioAltaSeguimentoAtendimentoController extends ActionController  {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1279501962536571125L;
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	@EJB
	private IAghuFacade aghuFacade;
	@EJB
	private IParametroFacade parametroFacade;
	@EJB
	private IFaturamentoApoioFacade faturamentoApoioFacade;
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	@EJB
	private IInternacaoFacade internacaoFacade;
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	/**
	 * Slider Previsão de Consulta Ambulatorial
	 */
	private MpmAltaPedidoExame altaPedidoExame; // Alta pedido exame
	private MpmAltaSumario altaSumario; 
	private String textoInformativoHospital; // Texto informativo e parametrizado do hospital
	private VAinConvenioPlano convenioPlano; // Convênio
	private AghEspecialidades especialidade; // Especialidade
	private AghEquipes equipe; // Equipe
	private VRapServidorConselho servidorConselho; // Profissional ou Servidor
	private AghUnidadesFuncionais unidadeFuncional; // Zona ou unidade funcional
	private VAacSiglaUnfSala sala; // Sala
	private Date dataConsulta = new Date(); // Data da consulta
	private DominioSimNao conformeAgenda; // Conforme agenda
	private boolean exibirBotaoGravarPrevisaoConsultaAmbulatorial; // Flag do botão gravar
	private boolean exibirBotaoExcluirPrevisaoConsultaAmbulatorial; // Flag do botão excluir
	private Boolean exibeSliderPedidosExamesPosAlta;
	private Boolean exibeSliderPrevisaoConsultaAmbulatorial;
	
	
	/**
	 * Slider Reinternação
	 */
	private MpmAltaReinternacao altaReinternacao;
	private AghEspecialidades especialidadeReinternacao; // Especialidade
	private MpmMotivoReinternacao motivoReinternacao;
	private String observacaoReinternacao;
	private Date dataReinternacao;
	private boolean exibirBotaoExcluirReinternacao;
	private List<VAelExamesSolicitacao> pedidosExamesPosAlta;
	private VAelExamesSolicitacao vAelExamesSolicitacao;
	
	@PostConstruct
	public void init() {
		begin(conversation);
	}
	
	/**
	 * @param altaSumario
	 *  
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public void renderSeguimentoAtendimento(MpmAltaSumario altaSumario) throws BaseException {

		this.altaSumario = altaSumario;
		
		if (altaSumario != null) {
			
			this.altaReinternacao = this.prescricaoMedicaFacade.obterMpmAltaReinternacao(altaSumario.getId().getApaAtdSeq(), altaSumario.getId().getApaSeq(), altaSumario.getId().getSeqp());
			this.altaPedidoExame = this.prescricaoMedicaFacade.obterMpmAltaPedidoExame(altaSumario.getId().getApaAtdSeq(), altaSumario.getId().getApaSeq(),altaSumario.getId().getSeqp());
			
			if (altaPedidoExame != null) { // Alta pedido exame existe...
				
				if (altaPedidoExame.getFatConvenioSaudePlano() != null) {
					
					this.convenioPlano = this.internacaoFacade.obterVAinConvenioPlanoAtivoPeloId(altaPedidoExame.getFatConvenioSaudePlano().getId().getSeq(),altaPedidoExame.getFatConvenioSaudePlano().getId().getCnvCodigo());	
				
				}	
				
				this.equipe = this.altaPedidoExame.getAghEquipes();
				this.especialidade = this.altaPedidoExame.getAghEspecialidades();
				this.unidadeFuncional = this.altaPedidoExame.getAghUnidadesFuncionais();
				
				if (altaPedidoExame.getAghProfEspecialidades() != null) {
					
					this.servidorConselho = this.registroColaboradorFacade.obterVRapServidorConselhoPeloId(altaPedidoExame.getAghProfEspecialidades().getId().getSerMatricula(), altaPedidoExame.getAghProfEspecialidades().getId().getSerVinCodigo(), null);
			
				}
				
				if (altaPedidoExame.getAacUnidFuncionalSalas() != null) {
					
					this.sala = this.ambulatorioFacade.obterVAacSiglaUnfSalaPeloId(altaPedidoExame.getAacUnidFuncionalSalas().getId().getSala(), altaPedidoExame.getAacUnidFuncionalSalas().getId().getUnfSeq());	
				
				}

				this.dataConsulta = this.altaPedidoExame.getDthrConsulta() == null ? null : this.altaPedidoExame.getDthrConsulta();
				this.conformeAgenda = this.altaPedidoExame.getIndAgenda() == null ? null : this.altaPedidoExame.getIndAgenda() == Boolean.TRUE ? DominioSimNao.S : DominioSimNao.N;
				
				setExibirBotaoExcluirPrevisaoConsultaAmbulatorial(true);
				setExibirBotaoGravarPrevisaoConsultaAmbulatorial(true);
				
				carregarPedidosExamesPosAlta();
				
				exibeSliderPedidosExamesPosAlta = true;
				
			} else {  // Alta pedido exame NÃO existe...
				
				this.altaPedidoExame = new MpmAltaPedidoExame(); 
				
				final AghParametros paramPlano = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_PLANO_SUS_AMB);
				final AghParametros paramConvenio = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_CONVENIO_SUS);
				this.convenioPlano = this.internacaoFacade.obterVAinConvenioPlanoSus(paramPlano, paramConvenio);
				if(convenioPlano == null){
					apresentarMsgNegocio(Severity.WARN, "ERRO_LOCALIZAR_CONVENIO");
				}
				limparCamposPrevisaoConsultaAmbulatorial();
				setExibirBotaoExcluirPrevisaoConsultaAmbulatorial(false);
				setExibirBotaoGravarPrevisaoConsultaAmbulatorial(true);
				this.exibeSliderPedidosExamesPosAlta = false;
				
			}
			
			if (altaReinternacao != null) {
				
				this.especialidadeReinternacao = altaReinternacao.getEspecialidade();
				this.motivoReinternacao = altaReinternacao.getMotivoReinternacao();
				this.observacaoReinternacao = altaReinternacao.getObservacao();
				this.dataReinternacao = altaReinternacao.getData();
				this.exibirBotaoExcluirReinternacao = true;
				
			} else {
				
				altaReinternacao = new MpmAltaReinternacao();
				this.exibirBotaoExcluirReinternacao = false;
				
			}
			
		}
		
		AghParametros parametro = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_MSG_SEGUIMENTO_ATENDIMENTO);
		
		if (parametro != null && parametro.getVlrTexto() != null) {
			
			textoInformativoHospital = parametro.getVlrTexto();
			
		}
		
		DominioGrupoConvenio grupoConvenio = altaSumario.getConvenioSaudePlano().getConvenioSaude().getGrupoConvenio();
		
		if (grupoConvenio != null && grupoConvenio.equals(DominioGrupoConvenio.S)) {
			
			exibeSliderPrevisaoConsultaAmbulatorial = true;
			
		} else {
			
			exibeSliderPrevisaoConsultaAmbulatorial = false;
			exibeSliderPedidosExamesPosAlta = false;
			
		}
		
	}
	
	/**
	 * Limpa ambos os campos do formulário de previsão de consulta ambulatorial
	 */
	public void limparCamposPrevisaoConsultaAmbulatorial() {
		setAltaPedidoExame(new MpmAltaPedidoExame());
		setEspecialidade(null);
		limparCamposPrevisaoConsultaAmbulatorialEquipeProfissional();
		setUnidadeFuncional(null);
		limparCamposPrevisaoConsultaAmbulatorialSala();
		setConformeAgenda(null);		
		setDataConsulta(null);
	}
	
	/**
	 * Limpa campos do formulário de previsão de consulta ambulatorial
	 * Ao selecionar uma nova especialidade os campos equipe e profissional serão limpos
	 */
	public void limparCamposEspecialidade() {
		this.limparCamposPrevisaoConsultaAmbulatorialEquipeProfissional();
		this.limparCamposPrevisaoConsultaAmbulatorialProfissional();
	}
	
	
	
	
	/**
	 * Limpa campos do formulário de previsão de consulta ambulatorial
	 * Ao selecionar uma nova especialidade os campos equipe e profissional serão limpos
	 */
	public void limparCamposPrevisaoConsultaAmbulatorialEquipeProfissional() {
		setEquipe(null);
	}
	
	
	/**
	 * Limpa campos do formulário de previsão de consulta ambulatorial
	 * Ao selecionar uma nova especialidade os campos equipe e profissional serão limpos
	 */
	public void limparCamposPrevisaoConsultaAmbulatorialProfissional() {
		setServidorConselho(null);
	}
	
	/**
	 * Limpa campos do formulário de previsão de consulta ambulatorial
	 * Ao selecionar uma nova zona/unidade funcional o campo sala será limpo
	 */
	public void limparCamposPrevisaoConsultaAmbulatorialSala() {
		setSala(null);
	}

	
	/**
	 * Grava as informações do Slider 3 - Reinternacao
	 */
	public void gravarReinternacao() {
		
		Boolean insercao = Boolean.TRUE;
		
		if (altaReinternacao.getId() != null) {
			insercao = Boolean.FALSE;
		}
		
		altaReinternacao.setAltaSumario(altaSumario);
		altaReinternacao.setEspecialidade(especialidadeReinternacao);
		altaReinternacao.setDescEspecialidade(especialidadeReinternacao.getNomeEspecialidade());
		altaReinternacao.setMotivoReinternacao(motivoReinternacao);
		altaReinternacao.setObservacao(observacaoReinternacao);
		altaReinternacao.setData(dataReinternacao);
		aplicarMinusculasMpmAltaReinternacao(altaReinternacao);
		try {
			
			this.prescricaoMedicaFacade.gravarAltaReinternacao(altaReinternacao);
		
			if (insercao) {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_REINTERNACAO_01");
			} else {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_REINTERNACAO_02");
			}
			
		} catch (BaseException e) {
			
			apresentarExcecaoNegocio(e);
			
		}
		
		exibirBotaoExcluirReinternacao = true;
		
	}
	
	private void aplicarMinusculasMpmAltaReinternacao(MpmAltaReinternacao ar) {
		ar.setDescEspecialidade(WordUtils.capitalizeFully(ar.getDescEspecialidade()));	
	}
	
	/**
	 * Remove as informações do Slider 3 - Reinternacao
	 */
	public void excluirReinternacao() {
		
		try {
			
			this.prescricaoMedicaFacade.removerAltaReinternacao(altaReinternacao);
			
		} catch (BaseException e) {
			
			apresentarExcecaoNegocio(e);
			
		}
		
		altaReinternacao = new MpmAltaReinternacao();
		especialidadeReinternacao = null;
		motivoReinternacao = null;
		dataReinternacao = null;
		observacaoReinternacao = null;
		exibirBotaoExcluirReinternacao = false;
		apresentarMsgNegocio(Severity.INFO, "Reinternação removida com sucesso.");
		
	}
	
	// Metódo para Suggestion Box de Convênio
	public List<VAinConvenioPlano> obterConvenio(Object objPesquisa){
		return this.internacaoFacade.pesquisarConveniosAtivos(objPesquisa);
	}
	
	// Metódo para Suggestion Box de Especialidade
	public List<AghEspecialidades> obterEspecialidade(String objPesquisa){
		//return this.prescricaoMedicaFacade.listarEspecialidadesAtivas(objPesquisa);
		return this.prescricaoMedicaFacade.listarEspecialidadesAtivas(objPesquisa);
	}
	
	// Metódo para Suggestion Box de Especialidade da Reinternação
	public List<AghEspecialidades> obterEspecialidadeReinternacao(String objPesquisa){
		//return this.prescricaoMedicaFacade.listarEspecialidadesAtivas(objPesquisa);
		return this.aghuFacade.pesquisarEspecialidades(objPesquisa);
	}

	// Metódo para Suggestion Box de Equipe
	public List<AghEquipes> obterEquipe(String objPesquisa) throws BaseException  {
		if(especialidade == null){
			apresentarMsgNegocio(Severity.WARN, "MENSAGEM_SELECIONE_ESPECIALIDADE_EQUIPE");
			return null;
		}else{
			return this.prescricaoMedicaFacade.getListaEquipesPorEspecialidade(objPesquisa, getEspecialidade(), DominioSituacao.A);
		}
		
	}
	
	// Metódo para Suggestion Box de Profissional/Servidor
	public List<VRapServidorConselho> obterProfissional(String objPesquisa) throws BaseException  {
		if(especialidade == null){
			apresentarMsgNegocio(Severity.WARN, "MENSAGEM_SELECIONE_ESPECIALIDADE_PROFISSIONAL");
			return null;
		}else{
			return this.prescricaoMedicaFacade.getListaProfissionaisPorEspecialidade(objPesquisa, getEspecialidade());
		}
	}
	
	// Metódo para Suggestion Box de MpmMotivoReinternacao
	public List<MpmMotivoReinternacao> obterMotivoReinternacao(String objPesquisa){
		return this.prescricaoMedicaFacade.obterMpmMotivoReinternacao(objPesquisa);
	}
	
	// Metódo para Suggestion Box de Zona
	public List<AghUnidadesFuncionais> obterZona(String objPesquisa) throws BaseException  {
		return this.aghuFacade.listarUnidadeFuncionalPorFuncionalSala(objPesquisa);
	}
	
	// Metódo para Suggestion Box de Sala
	public List<VAacSiglaUnfSala> obterSala(String objPesquisa) throws BaseException  {
		
		if (unidadeFuncional == null) {
			
			apresentarMsgNegocio(Severity.WARN, "MENSAGEM_SELECIONE_ZONA_SALA");
			return null;
			
		} else {
			return this.aghuFacade.pesquisarSalasUnidadeFuncional(objPesquisa, getUnidadeFuncional());
		}
	}
			
	/**
	 * Gravar seguimento de atendimento
	 *  
	 *  
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public void gravarSeguimentoAtendimento() throws BaseException{
		
		//if(this.isValidaCamposRequeridosEmBranco()){

		// Convênio
		if (this.convenioPlano != null) {
			
			FatConvenioSaudePlano fatConvenioSaudePlano = this.faturamentoApoioFacade.obterConvenioSaudePlano(convenioPlano.getId().getCnvCodigo(), convenioPlano.getId().getPlano());
				this.altaPedidoExame.setDescConvenio(fatConvenioSaudePlano.getConvenioSaude().getDescricao());
			this.altaPedidoExame.setDescPlanoConvenio(fatConvenioSaudePlano.getDescricaoPlanoConvenio());
			this.altaPedidoExame.setFatConvenioSaudePlano(fatConvenioSaudePlano);
			
		} else {
			
			this.altaPedidoExame.setDescConvenio(null);
			this.altaPedidoExame.setDescPlanoConvenio(null);
			this.altaPedidoExame.setFatConvenioSaudePlano(null);
			
		}
		
		
		// Conforme Agenda
		if (this.conformeAgenda != null && StringUtils.isNotEmpty(conformeAgenda.getDescricao())) {
			
			this.altaPedidoExame.setIndAgenda(this.conformeAgenda.isSim());
			
		} else {
			
			this.altaPedidoExame.setIndAgenda(null);
			
		}
		
		// Especialidade
			if (this.especialidade != null){
		this.altaPedidoExame.setAghEspecialidades(this.especialidade);
				this.altaPedidoExame.setDescEspecialidade(this.especialidade.getNomeEspecialidade());
			}
		
		// Equipe
		if (this.equipe != null) {
			
			this.altaPedidoExame.setAghEquipes(this.equipe);
			this.altaPedidoExame.setDescEquipe(this.equipe.getNome());
			
		} else {
			
			this.altaPedidoExame.setAghEquipes(null);
			this.altaPedidoExame.setDescEquipe(null);
			
		}

		// Profissional
		if (this.servidorConselho != null) {
			
			AghProfEspecialidadesId aghProfEspecialidadesId = new AghProfEspecialidadesId();
			aghProfEspecialidadesId.setEspSeq(this.especialidade.getSeq());
			aghProfEspecialidadesId.setSerMatricula(this.servidorConselho.getId().getMatricula());
			aghProfEspecialidadesId.setSerVinCodigo(this.servidorConselho.getId().getVinCodigo());
			AghProfEspecialidades aghProfEspecialidades = this.cadastrosBasicosInternacaoFacade.obterProfEspecialidades(aghProfEspecialidadesId);
			this.altaPedidoExame.setAghProfEspecialidades(aghProfEspecialidades);
				this.altaPedidoExame.setDescProfissional(this.servidorConselho.getNome());
			
		} else {
			
			this.altaPedidoExame.setAghProfEspecialidades(null);
				this.altaPedidoExame.setDescProfissional(null);
			
		}
		

		this.altaPedidoExame.setDthrConsulta(this.dataConsulta);

	
	
		// Unidade funcional
		if (this.unidadeFuncional != null) {
			
			this.altaPedidoExame.setAghUnidadesFuncionais(this.unidadeFuncional);
			this.altaPedidoExame.setDescUnidade(this.unidadeFuncional.getDescricao());
			
		} else {
			
			this.altaPedidoExame.setAghUnidadesFuncionais(null);
			this.altaPedidoExame.setDescUnidade(null);
			
		}
		
		// Sala
		if (this.sala != null) {
			
			AacUnidFuncionalSalas aacUnidFuncionalSalas = this.ambulatorioFacade.obterUnidFuncionalSalasPeloId(this.sala.getId().getUnfSeq(), this.sala.getId().getSala());
			aacUnidFuncionalSalas.setUnidadeFuncional(this.unidadeFuncional);
			this.altaPedidoExame.setAacUnidFuncionalSalas(aacUnidFuncionalSalas);
			
		} else {
			
			this.altaPedidoExame.setAacUnidFuncionalSalas(null);
			
		}
		
		// Alta Sumario
		this.altaPedidoExame.setAltaSumario(this.altaSumario);
		
		// Tipo de pedido
		this.altaPedidoExame.setTipoPedido(DominioTipoPedido.P);

			aplicarMinusculasMpmAltaPedidoExame(this.altaPedidoExame);
	
		try {
			
			// Insere ou atualiza Alta Pedido Exame
			String confirmacao  = this.prescricaoMedicaFacade.gravarAltaPedidoExame(this.altaPedidoExame);
			setExibirBotaoExcluirPrevisaoConsultaAmbulatorial(true);
			carregarPedidosExamesPosAlta();
			exibeSliderPedidosExamesPosAlta = true;	
			apresentarMsgNegocio(Severity.INFO, confirmacao);
			
		} catch (BaseException e) {
			
			apresentarExcecaoNegocio(e);
			this.altaPedidoExame.setId(null);
			
		}
	//}
		
	} 
	

	private void aplicarMinusculasMpmAltaPedidoExame(MpmAltaPedidoExame ape) {
		ape.setDescEquipe(WordUtils.capitalizeFully(ape.getDescEquipe()));
		ape.setDescEspecialidade(WordUtils.capitalizeFully(ape.getDescEspecialidade()));
		ape.setDescUnidade(WordUtils.capitalizeFully(ape.getDescUnidade()));
		ape.setDescConvenio(WordUtils.capitalizeFully(ape.getDescConvenio()));
		ape.setDescPlanoConvenio(WordUtils.capitalizeFully(ape.getDescPlanoConvenio()));
		ape.setDescProfissional(WordUtils.capitalizeFully(ape.getDescProfissional()));

	}
	
	/*private String colocarMaiusculoComecoEMinusculoResto(
			String descricao) {
		String retorno = "";
		if(descricao != null && StringUtils.isNotBlank(descricao))
			retorno = descricao.substring(0,1).toUpperCase().concat(descricao.substring(1, descricao.length()).toLowerCase());
		return retorno;
	}*/

	/**
	 * Excluir seguimento de atendimento
	 */
	public void excluirSeguimentoAtendimento(){	
		try {
			this.prescricaoMedicaFacade.removerAltaPedidoExame(altaPedidoExame);
			limparCamposPrevisaoConsultaAmbulatorial();
			setExibirBotaoExcluirPrevisaoConsultaAmbulatorial(false);
			pedidosExamesPosAlta = null;
			exibeSliderPedidosExamesPosAlta = false;
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_ALTA_PREVISAO_CONSULTA_AMBULATORIAL");
		} catch (BaseException e) {	
			apresentarExcecaoNegocio(e);
		}
	}
	
	public List<VAelExamesSolicitacao> obterNomeExames(String objPesquisa){
		List<VAelExamesSolicitacao> nomeExames = this.prescricaoMedicaFacade.obterNomeExames(objPesquisa);
		return nomeExames;
	}
	
	/**
	 * 
	 * @param EXAMES
	 */
	public void excluirPedidosExamesPosAlta(VAelExamesSolicitacao vAelExamesSolicitacao) {
		try{
			MpmAltaItemPedidoExame altaItemPedidoExame = null;
			i:
			for(VAelExamesSolicitacao pedidos : pedidosExamesPosAlta){
				if(pedidos.getId().getDescricaoUsualExame().equalsIgnoreCase(vAelExamesSolicitacao.getId().getDescricaoUsualExame())){
					altaItemPedidoExame = pedidos.getPedidoExame();
					break i;
				}
			}
			this.prescricaoMedicaFacade.excluirMpmAltaItemPedidoExame(altaItemPedidoExame);
			carregarPedidosExamesPosAlta();
			apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_EXCLUSAO_ALTA_ITEM_PEDIDO_EXAME");
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}
	
	public void gravarPedidosExamesPosAlta(){
		try{
			MpmAltaItemPedidoExame item = new MpmAltaItemPedidoExame();
			item.setDescExame(this.vAelExamesSolicitacao.getId().getDescricaoUsualExame());
			AelUnfExecutaExames executaExames = this.prescricaoMedicaFacade.buscarAelUnfExecutaExamesPorID(this.vAelExamesSolicitacao.getId().getManSeq(),this.vAelExamesSolicitacao.getId().getSigla(),this.vAelExamesSolicitacao.getId().getUnfSeq());
			
			item.setAelUnfExecutaExames(executaExames);			
			item.setMpmAltaPedidoExames(this.altaPedidoExame);
			item.setMpmAltaSumarios(this.altaSumario); 
			aplicarMinusculasMpmAltaItemPedidoExame(item);
			this.prescricaoMedicaFacade.inserirAltaItemPedidoExame(item);
			
			carregarPedidosExamesPosAlta();
			this.vAelExamesSolicitacao = null;
			apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_CRIACAO_ALTA_ITEM_PEDIDO_EXAME");
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}
	
	private void aplicarMinusculasMpmAltaItemPedidoExame(MpmAltaItemPedidoExame aipe) {
		aipe.setDescExame(WordUtils.capitalizeFully(aipe.getDescExame()));		
	}
	
	private void carregarPedidosExamesPosAlta(){
		try{
			pedidosExamesPosAlta = new ArrayList<VAelExamesSolicitacao>();
		
		List<MpmAltaItemPedidoExame> pedidos = this.prescricaoMedicaFacade.obterMpmAltaItemPedidoExame(altaPedidoExame.getId().getApaAtdSeq(), altaPedidoExame.getId().getApaSeq(), altaPedidoExame.getId().getSeqp());
		for(MpmAltaItemPedidoExame item : pedidos){
			VAelExamesSolicitacao exame = new VAelExamesSolicitacao();
			exame.setId(new VAelExamesSolicitacaoId(item.getDescExame()));
			AelMateriaisAnalises materialAnalise = this.prescricaoMedicaFacade.buscarAelMateriaisAnalisesPorAelUnfExecutaExames(item.getAelUnfExecutaExames());
			exame.setDescricaoMaterial(materialAnalise.getDescricao());
			
			AghUnidadesFuncionais unf = this.prescricaoMedicaFacade.buscarAghUnidadesFuncionaisPorAelUnfExecutaExames(item.getAelUnfExecutaExames());
			exame.setDescricaoUnidade(unf.getDescricao());
			exame.setPedidoExame(item);
			
			pedidosExamesPosAlta.add(exame);
			
		}
		
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * @param especialidade the especialidade to set
	 */
	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
		this.equipe = null;
		this.servidorConselho = null;
	}

	/**
	 * @return the especialidade
	 */
	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	/**
	 * @param altaPedidoExame the altaPedidoExame to set
	 */
	public void setAltaPedidoExame(MpmAltaPedidoExame altaPedidoExame) {
		this.altaPedidoExame = altaPedidoExame;
	}

	/**
	 * @return the altaPedidoExame
	 */
	public MpmAltaPedidoExame getAltaPedidoExame() {
		return altaPedidoExame;
	}

	/**
	 * @param equipe the equipe to set
	 */
	public void setEquipe(AghEquipes equipe) {
		this.equipe = equipe;
	}

	/**
	 * @return the equipe
	 */
	public AghEquipes getEquipe() {
		return equipe;
	}

	/**
	 * @param convenioPlano the convenioPlano to set
	 */
	public void setConvenioPlano(VAinConvenioPlano convenioPlano) {
		this.convenioPlano = convenioPlano;
	}

	/**
	 * @return the convenioPlano
	 */
	public VAinConvenioPlano getConvenioPlano() {
		return convenioPlano;
	}

	/**
	 * @param textoInformativoHospital the textoInformativoHospital to set
	 */
	public void setTextoInformativoHospital(String textoInformativoHospital) {
		this.textoInformativoHospital = textoInformativoHospital;
	}

	/**
	 * @return the textoInformativoHospital
	 */
	public String getTextoInformativoHospital() {
		return textoInformativoHospital;
	}

	public List<VAelExamesSolicitacao> getPedidosExamesPosAlta() {
		return pedidosExamesPosAlta;
	}

	public void setPedidosExamesPosAlta(List<VAelExamesSolicitacao> pedidosExamesPosAlta) {
		this.pedidosExamesPosAlta = pedidosExamesPosAlta;
	}

	public VAelExamesSolicitacao getvAelExamesSolicitacao() {
		return vAelExamesSolicitacao;
	}

	public void setvAelExamesSolicitacao(VAelExamesSolicitacao vAelExamesSolicitacao) {
		this.vAelExamesSolicitacao = vAelExamesSolicitacao;
	}

	public MpmAltaSumario getAltaSumario() {
		return altaSumario;
	}

	public void setAltaSumario(MpmAltaSumario altaSumario) {
		this.altaSumario = altaSumario;
	}

	public AghEspecialidades getEspecialidadeReinternacao() {
		return especialidadeReinternacao;
	}

	public void setEspecialidadeReinternacao(AghEspecialidades especialidadeReinternacao) {
		this.especialidadeReinternacao = especialidadeReinternacao;
	}

	public MpmAltaReinternacao getAltaReinternacao() {
		return altaReinternacao;
	}

	public void setAltaReinternacao(MpmAltaReinternacao altaReinternacao) {
		this.altaReinternacao = altaReinternacao;
	}

	public MpmMotivoReinternacao getMotivoReinternacao() {
		return motivoReinternacao;
	}

	public void setMotivoReinternacao(MpmMotivoReinternacao motivoReinternacao) {
		this.motivoReinternacao = motivoReinternacao;
	}

	public String getObservacaoReinternacao() {
		return observacaoReinternacao;
	}

	public void setObservacaoReinternacao(String observacaoReinternacao) {
		this.observacaoReinternacao = observacaoReinternacao;
	}

	public Date getDataReinternacao() {
		return dataReinternacao;
	}

	public void setDataReinternacao(Date dataReinternacao) {
		this.dataReinternacao = dataReinternacao;
	}

	public boolean isExibirBotaoExcluirReinternacao() {
		return exibirBotaoExcluirReinternacao;
	}

	public void setExibirBotaoExcluirReinternacao(
			boolean exibirBotaoExcluirReinternacao) {
		this.exibirBotaoExcluirReinternacao = exibirBotaoExcluirReinternacao;
	}

	public VRapServidorConselho getServidorConselho() {
		return servidorConselho;
	}

	public void setServidorConselho(VRapServidorConselho servidorConselho) {
		this.servidorConselho = servidorConselho;
	}

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public VAacSiglaUnfSala getSala() {
		return sala;
	}

	public void setSala(VAacSiglaUnfSala sala) {
		this.sala = sala;
	}

	public boolean isExibirBotaoGravarPrevisaoConsultaAmbulatorial() {
		return exibirBotaoGravarPrevisaoConsultaAmbulatorial;
	}

	public void setExibirBotaoGravarPrevisaoConsultaAmbulatorial(
			boolean exibirBotaoGravarPrevisaoConsultaAmbulatorial) {
		this.exibirBotaoGravarPrevisaoConsultaAmbulatorial = exibirBotaoGravarPrevisaoConsultaAmbulatorial;
	}

	public boolean isExibirBotaoExcluirPrevisaoConsultaAmbulatorial() {
		return exibirBotaoExcluirPrevisaoConsultaAmbulatorial;
	}

	public void setExibirBotaoExcluirPrevisaoConsultaAmbulatorial(
			boolean exibirBotaoExcluirPrevisaoConsultaAmbulatorial) {
		this.exibirBotaoExcluirPrevisaoConsultaAmbulatorial = exibirBotaoExcluirPrevisaoConsultaAmbulatorial;
	}

	public Boolean getExibeSliderPedidosExamesPosAlta() {
		return exibeSliderPedidosExamesPosAlta;
	}

	public void setExibeSliderPedidosExamesPosAlta(
			Boolean exibeSliderPedidosExamesPosAlta) {
		this.exibeSliderPedidosExamesPosAlta = exibeSliderPedidosExamesPosAlta;
	}

	/**
	 * @param conformeAgenda the conformeAgenda to set
	 */
	public void setConformeAgenda(DominioSimNao conformeAgenda) {
		this.conformeAgenda = conformeAgenda;
	}

	/**
	 * @return the conformeAgenda
	 */
	public DominioSimNao getConformeAgenda() {
		return conformeAgenda;
	}

	
	public Date getDataConsulta() {
		return dataConsulta;
	}

	public void setDataConsulta(Date dataConsulta) {
		this.dataConsulta = dataConsulta;
	}

	public Boolean getExibeSliderPrevisaoConsultaAmbulatorial() {
		return exibeSliderPrevisaoConsultaAmbulatorial;
	}

	public void setExibeSliderPrevisaoConsultaAmbulatorial(
			Boolean exibeSliderPrevisaoConsultaAmbulatorial) {
		this.exibeSliderPrevisaoConsultaAmbulatorial = exibeSliderPrevisaoConsultaAmbulatorial;
	}

}