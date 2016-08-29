package br.gov.mec.aghu.exames.coleta.action;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.action.ActionController;
//import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioSituacaoAmostra;
import br.gov.mec.aghu.exames.business.IExamesBeanFacade;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.coleta.business.IColetaExamesFacade;
import br.gov.mec.aghu.exames.coleta.vo.AelExamesAmostraVO;
import br.gov.mec.aghu.exames.vo.VAelSolicAtendsVO;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AelAmostraItemExamesId;
import br.gov.mec.aghu.model.AelAmostras;
import br.gov.mec.aghu.model.AelExtratoAmostras;
import br.gov.mec.aghu.model.AelSalasExecutorasExames;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;

/**
 * Controller para tela de Preencher Informação da Amostra  
 * @author gzapalaglio
 *
 */


@SuppressWarnings("PMD.AghuTooManyMethods")
public class InformacaoAmostraController extends ActionController {

	private static final String EXCECAO_CAPTURADA = "Exceção capturada:";


	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(InformacaoAmostraController.class);

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1302029157979491597L;
	
	@EJB
	private IColetaExamesFacade coletaExamesFacade;
	
	@EJB
	private IExamesBeanFacade examesBeanFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@Inject
	private InformacaoColetaController informacaoColetaController;

	
	// Parâmetros Obrigatórios
	private Short hedGaeUnfSeq;
	private Integer hedGaeSeqp;
	private Date hedDthrAgenda;
	private Short unfSeq;
	

	// Informações
	private List<VAelSolicAtendsVO> listaSolicitacoes;
	
	private Boolean possuiCaracteristica;
	private Boolean desabilitaBotaoColeta;
	private Boolean desabilitaBotaoCancelarColeta;
	private Boolean desabilitaBotaoColetarExame;
	private Boolean desabilitaBotaoVoltarExame;

	// Lista de Amostras
	private List<AelAmostras> listaAmostras;
	
	// Lista de Exames da Amostra
	private List<AelExamesAmostraVO> listaExamesAmostra;
	
	// Solicitação Selecionada
	private Integer soeSeqSelecionado;

	// Amostra Selecionada
	private AelAmostras amostraSelecionada;
	private Short seqpSelecionado;
	
	// Sala
	private AelSalasExecutorasExames sala;
	
	// Unidade Executora
	private AghUnidadesFuncionais unidadeExecutora;
	
	private Integer soeSeq;
	
	private Short amostraSeq;
	
	private Boolean pesquisaOk;
	
	
	public Boolean getPrimeiraPesquisa() {
		return primeiraPesquisa;
	}

	public void setPrimeiraPesquisa(Boolean primeiraPesquisa) {
		this.primeiraPesquisa = primeiraPesquisa;
	}

	private Integer prontuario;
	
	private Integer pacCodigo;

	private AipPacientes paciente;
	
	private String pacNome;
	
	private Integer pacCodigoFonetica;
	
	private String cameFrom;
	
	private Boolean origemMenu;
	
	private Integer selectedTab;
	
	private final Integer TAB_1 = 0;
	private final Integer TAB_2 = 1;
	
	private Boolean primeiraPesquisa = false;
	
	private Boolean isAcaoLimpar = false;
	
	public void inicio() {
		if(!isAcaoLimpar){
			this.limpar();
			try {
				if("pesquisaAgendaExamesHorarios".equals(this.getCameFrom())){
					this.origemMenu = false;
					selectedTab = TAB_1;
					// msg de sucesso após a execução da estória #5428 (atualiza amostras para situacao 'Coletada')
					apresentarMsgNegocio(Severity.INFO, "AEL_00872");
				} else {
					this.origemMenu = true;
					selectedTab = TAB_2;
//					return;
				}
				if(unfSeq!=null){
					possuiCaracteristica = this.aghuFacade.unidadeFuncionalPossuiCaracteristica(unfSeq,ConstanteAghCaractUnidFuncionais.UNID_COLETA);
					unidadeExecutora = this.aghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(unfSeq);
				} 
				if(hedGaeUnfSeq!=null && hedGaeSeqp!=null && hedDthrAgenda!=null && unfSeq!=null) {
					this.listaSolicitacoes = this.coletaExamesFacade.obterSolicAtendsPorItemHorarioAgendado(hedGaeUnfSeq,hedGaeSeqp,hedDthrAgenda);
					if(this.listaSolicitacoes!=null && this.listaSolicitacoes.size()>0){
						VAelSolicAtendsVO solicitacao = this.listaSolicitacoes.get(0);
						this.soeSeqSelecionado = solicitacao.getNumero();
						this.obterAmostrasDaSolicitacao();
						if(!this.origemMenu) {
							this.imprimirTodasEtiquetasAmostra();
						}
					}
				}
				if(pacCodigoFonetica!=null) {
					this.setPacCodigo(pacCodigoFonetica);
					this.setProntuario(null);
					this.selecionarPacienteConsulta();
					if (selectedTab == null) {
						selectedTab = TAB_2;
					}
				}
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
		} else {
			selectedTab = TAB_2;
		}		
		this.informacaoColetaController.setOrigemMenu(this.getOrigemMenu());
		this.informacaoColetaController.iniciar();
	
	}

	public void pesquisaPaciente(ValueChangeEvent event){
		try {
			paciente = pacienteFacade.pesquisarPacienteComponente(event.getNewValue(), event.getComponent().getId());
		}catch(BaseException e){
			apresentarExcecaoNegocio(e);
		}
	}
	/**
	 * Obter as amostras por Solicitação de Exame Seq
	 * 
	 */
	public void obterAmostrasDaSolicitacao() {
		if(amostraSeq!=null){
			AelAmostras amostra = this.coletaExamesFacade.obterAmostra(this.soeSeq, this.amostraSeq);
			this.listaAmostras = new ArrayList<AelAmostras>();
			this.listaAmostras.add(amostra);
		} else {
			this.listaAmostras = this.coletaExamesFacade.buscarAmostrasPorSolicitacaoExame(soeSeqSelecionado);	
		}
		if(this.listaAmostras != null && !this.listaAmostras.isEmpty()){
			this.seqpSelecionado =this.listaAmostras.get(0).getId().getSeqp();
			this.obterExamesDaAmostra();
		} else {
			this.listaAmostras = null;
			this.seqpSelecionado = null;
			this.listaExamesAmostra = null;
		}
	}
	
	/**
	 * Obtem a lista de Exames da Amostra
	 * 
	 */
	public void obterExamesDaAmostra(){
		this.sala = null;
		this.listaExamesAmostra = this.coletaExamesFacade.obterAmostraItemExamesPorAmostra(this.soeSeqSelecionado, this.seqpSelecionado);
		amostraSelecionada = this.examesFacade.buscarAmostrasComRecepientePorId(this.soeSeqSelecionado, this.seqpSelecionado);
		if(!(possuiCaracteristica &&  this.coletaExamesFacade.verificaSituacaoAmostraGeradaOuEmColeta(amostraSelecionada.getSituacao()))) {
			setDesabilitaBotaoColeta(Boolean.TRUE);
		}
		else {
			setDesabilitaBotaoColeta(Boolean.FALSE);
		}
		if(!(possuiCaracteristica && this.coletaExamesFacade.verificaSituacaoAmostraCURMA(amostraSelecionada.getSituacao()))) {
			setDesabilitaBotaoCancelarColeta(Boolean.TRUE);
		}
		else {
			setDesabilitaBotaoCancelarColeta(Boolean.FALSE);
		}
	}
	
	/**
	 * Obtem a situação anterior da Amostra
	 * 
	 * @param amoSoeSeq, amoSeqp
	 */
	public String obterSituacaoAnteriorAmostra(Integer amoSoeSeq, Short amoSeqp) {
		String situacaoAnterior = "";
		AelExtratoAmostras extrato =  this.coletaExamesFacade.pesquisarExtratoAmostraAnterior(amoSoeSeq, amoSeqp);
		if(extrato!=null) {
			situacaoAnterior = extrato.getSituacao().getDescricao();
		}
		return situacaoAnterior;
	}
	
	/**
	 * Obtem a descrição da Unidade Funcional pelo Código
	 * 
	 * @param unfSeq
	 */
	public String obterDescricaoUnidadeFuncional(Short unfSeq){
		AghUnidadesFuncionais unidadeFuncional = this.aghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(unfSeq);
		if(unidadeFuncional!=null) {
			return unidadeFuncional.getDescricao();
		}
		else {
			return "";
		}
	}
	
	/**
	 * Retorna o nome do paciente pelo código
	 * 
	 * @param pacCodigo
	 */
	public String obterNomePacientePorPacCodigo(Integer pacCodigo) {
		AipPacientes paciente;
		String nomePaciente = "";

		paciente = pacienteFacade.obterPaciente(pacCodigo);
		if(paciente!=null) {
			nomePaciente = paciente.getNome();
		}
		
		return nomePaciente;
	}
	
	public void limpar() {
		this.listaAmostras = null;
		this.listaExamesAmostra = null;
		this.soeSeqSelecionado = null;
		this.seqpSelecionado = null;
		this.amostraSelecionada = null;
		this.desabilitaBotaoColeta = true;
		this.desabilitaBotaoCancelarColeta = true;
		this.desabilitaBotaoColetarExame = true;
		this.desabilitaBotaoVoltarExame = true;
		this.sala = null;
		this.primeiraPesquisa = false;
	}

	public void limparPesquisa() {
		this.isAcaoLimpar = true;
		this.limpar();
		this.soeSeq = null;
		this.amostraSeq = null;
		this.pacCodigo = null;
		this.pacCodigoFonetica = null;
		this.pacNome = null;
		this.prontuario = null;
		this.paciente = null;
		this.listaSolicitacoes = null;
		this.unidadeExecutora = null;
		this.listaAmostras = null;
		this.listaExamesAmostra = null;
		informacaoColetaController.limparPaciente();
	}
	/**
	 * Verifica situação da Amostra se é G (gerada) ou M (em coleta).
	 * 
	 * @param situacaoAmostra
	 */
	public Boolean verificaSituacaoAmostraGeradaOuEmColeta(DominioSituacaoAmostra situacaoAmostra) {
		return this.coletaExamesFacade.verificaSituacaoAmostraGeradaOuEmColeta(situacaoAmostra);
	}
		
	/**
	 * Truncar a descricao
	 */
	public String truncarDescricao(String descricao) {
		String texto = descricao;
		if(texto.length()>38) {
			texto = StringUtils.abbreviate(texto, 38);	
		}
		return texto;
	}
	
	/**
	 * Obter nome Convenio / Plano da Solicitação
	 */
	public String obterConvenioPlano(String convenio, Short cspCnvCodigo, Short cspSeq) {
//		StringBuffer convenioPlano = new StringBuffer(); 
//		convenioPlano.append(convenio);
		FatConvenioSaudePlano convenioSaudePlano = this.faturamentoFacade.obterConvenioSaudePlano(cspCnvCodigo,
				cspSeq.byteValue());
		if(convenioSaudePlano==null) {
			return convenio;
		} else {
			return convenioSaudePlano.getDescricaoPlanoConvenio();
		}
//		if(convenioSaudePlano!=null) {
//			convenioPlano.append(" / ");
//			convenioPlano.append(convenioSaudePlano.getDescricao());
//		}
//		
//		return convenioPlano.toString();
	}
	
	/**
	 * Método que realiza a pesquisa da Sala Executora de Exames por Unidade Funcional.
	 */
	public List<AelSalasExecutorasExames> pesquisarSalaExecutoraExamesPorUnidadeFuncional(String param) {
		return this.examesFacade.obterSalaExecutoraExamesPorUnidadeFuncional(unidadeExecutora, param);
	}
	
	/**
	 * Método que atualiza a Unidade/ Sala da Amostra.
	 */
	public void atualizarSalaExecutoraExamesDaAmostra() {
		amostraSelecionada.setSalasExecutorasExames(sala);
		try {
			
			this.examesFacade.atualizarAmostra(amostraSelecionada, true);
			this.apresentarMsgNegocio(Severity.INFO,"AMOSTRA_ATUALIZADA_COM_SUCESSO");
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}	
	}
	
	/**
	 * Confirmar a coleta
	 */
	public void confirmarColeta() {
		this.isAcaoLimpar = false;
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error(EXCECAO_CAPTURADA, e);
		}
		try {
			
			this.examesBeanFacade.atualizarSituacaoExamesAmostra(amostraSelecionada, nomeMicrocomputador);
			// Atualização das Informações dos Exames das Amostras
			this.listaAmostras = this.coletaExamesFacade.buscarAmostrasPorSolicitacaoExame(soeSeqSelecionado);
			this.listaExamesAmostra = this.coletaExamesFacade.obterAmostraItemExamesPorAmostra(this.soeSeqSelecionado, this.seqpSelecionado);
			setDesabilitaBotaoColeta(Boolean.TRUE);
			setDesabilitaBotaoCancelarColeta(Boolean.FALSE);
			this.apresentarMsgNegocio(Severity.INFO,"AMOSTRA_COLETADA_COM_SUCESSO");
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}	
	}
	
	public void coletarExame() {
		List<AelExamesAmostraVO> listaSelecionados =  new ArrayList<AelExamesAmostraVO>();
		for(AelExamesAmostraVO exameAmostraVO: listaExamesAmostra){
			if(exameAmostraVO.getSelecionado() != null && exameAmostraVO.getSelecionado()){
				listaSelecionados.add(exameAmostraVO);	
			}
		}
		if(listaSelecionados.isEmpty()){
			this.apresentarMsgNegocio(Severity.WARN, "MENSAGEM_NENHUM_ITEM_EXAME_AMOSTRA_SELECIONADO");
			return;
		}
		
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error(EXCECAO_CAPTURADA, e);
		}
		
		try {
			
			this.coletaExamesFacade.validarColetaExames(this.getSeqpSelecionado().intValue(), listaSelecionados);
			for(AelExamesAmostraVO exameAmostraVO:listaSelecionados){
				AelAmostraItemExamesId id = new AelAmostraItemExamesId();
				id.setAmoSeqp(this.getSeqpSelecionado().intValue());
				id.setAmoSoeSeq(exameAmostraVO.getSoeSeq());
				id.setIseSoeSeq(exameAmostraVO.getSoeSeq());
				id.setIseSeqp(exameAmostraVO.getSeqp());
				this.coletaExamesFacade.coletarExame(id, nomeMicrocomputador);	
			}
			
			this.obterAmostrasDaSolicitacao();
			this.obterExamesDaAmostra();
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(),e);
		}
	}
	
	public void voltarExame() {
		
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error(EXCECAO_CAPTURADA, e);
		}
		
		try {
			List<AelExamesAmostraVO> listaSelecionados =  new ArrayList<AelExamesAmostraVO>();
			for(AelExamesAmostraVO exameAmostraVO: listaExamesAmostra){
				if(exameAmostraVO.getSelecionado() != null && exameAmostraVO.getSelecionado()){
					listaSelecionados.add(exameAmostraVO);	
				}
			}
			if(listaSelecionados.isEmpty()){
				this.apresentarMsgNegocio(Severity.WARN, "MENSAGEM_NENHUM_ITEM_EXAME_AMOSTRA_SELECIONADO");
				return;
			}
		
			this.coletaExamesFacade.validarVoltaExames(this.getSeqpSelecionado().intValue(), listaSelecionados);
			for(AelExamesAmostraVO exameAmostraVO: listaSelecionados){
				AelAmostraItemExamesId id = new AelAmostraItemExamesId();
				id.setAmoSeqp(this.getSeqpSelecionado().intValue());
				id.setAmoSoeSeq(exameAmostraVO.getSoeSeq());
				id.setIseSoeSeq(exameAmostraVO.getSoeSeq());
				id.setIseSeqp(exameAmostraVO.getSeqp());
				this.coletaExamesFacade.voltarExame(id, nomeMicrocomputador);	
			}
			this.obterAmostrasDaSolicitacao();
			this.obterExamesDaAmostra();
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(),e);
		}
	}
	
	public void manterBotoesExames() {
		this.desabilitaBotaoColetarExame = true;
		this.desabilitaBotaoVoltarExame = true;
		if(this.listaExamesAmostra != null) {
			for(AelExamesAmostraVO item : listaExamesAmostra) {
				if(BooleanUtils.isTrue(item.getSelecionado())) {
					this.desabilitaBotaoColetarExame = false;
					this.desabilitaBotaoVoltarExame = false;
					break;
				}
			}
		}
	}
	
	public void pesquisar(){
		try {
		this.isAcaoLimpar = false;
		this.primeiraPesquisa = true;
		selecionarPacienteConsulta();
		this.coletaExamesFacade.validarSolicitacaoPaciente(this.soeSeq, this.pacCodigo);
		this.coletaExamesFacade.validarSolicitacaoPorAmostra(this.soeSeq, this.amostraSeq);
		if(this.amostraSeq==null){
			this.listaSolicitacoes = this.coletaExamesFacade.pesquisarSolicitacaoPorPaciente(this.soeSeq, this.pacCodigo);
		} else {
			this.listaSolicitacoes = this.coletaExamesFacade.pesquisarSolicitacaoPorPacienteEAmostra(this.soeSeq, this.pacCodigo, this.amostraSeq);
		}
		if(listaSolicitacoes!=null && listaSolicitacoes.size()>0){
			VAelSolicAtendsVO vAelSolicAtendsVO = listaSolicitacoes.get(0);
			this.soeSeqSelecionado =  vAelSolicAtendsVO.getNumero();
			informacaoColetaController.setSoeSeqSelecionado(vAelSolicAtendsVO.getNumero());
			VAelSolicAtendsVO vo = listaSolicitacoes.get(0);
			if(amostraSeq==null){
				this.listaAmostras = this.coletaExamesFacade.buscarAmostrasPorSolicitacaoExame(vo.getNumero());	
			} else {
				this.listaAmostras = this.coletaExamesFacade.buscarAmostrasPorAmostraESolicitacaoExame(this.amostraSeq, vo.getNumero());
			}
				
			this.pesquisaOk = true;
		} else {
			this.listaAmostras = null;
			this.pesquisaOk = false;
		}
		if(this.seqpSelecionado != null){
			this.listaExamesAmostra = this.coletaExamesFacade.obterAmostraItemExamesPorAmostra(this.soeSeqSelecionado, this.seqpSelecionado);
		}else{
			this.listaExamesAmostra = null;
		}
		possuiCaracteristica = this.aghuFacade.unidadeFuncionalPossuiCaracteristica(unidadeExecutora.getSeq(),ConstanteAghCaractUnidFuncionais.UNID_COLETA);
		
		} catch (ApplicationBusinessException e) {
			this.pesquisaOk = false;
			this.apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(),e);
		}
		
	}
	
	/**
	 * Imprime a etiqueta de uma amostra
	 * 
	 * @param amostra
	 */
	public void imprimirEtiquetaAmostra() {
		this.isAcaoLimpar = false;
		try {
			String nomeMicrocomputador = getEnderecoRedeHostRemoto();
			this.examesFacade.imprimirEtiquetaAmostra(this.amostraSelecionada, this.unidadeExecutora, nomeMicrocomputador);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_IMPRESSAO_ETIQUETA", amostraSelecionada.getId().getSeqp());
		} catch (final BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (UnknownHostException e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
	/**
	 * Imprime as etiquetas de todas as amostras
	 * 
	 * @param amostra
	 */
	public void imprimirTodasEtiquetasAmostra() {
		this.isAcaoLimpar = false;

		List<AelAmostras> listAmostras = this.examesFacade
				.listarAmostrasPorAgendamento(hedGaeUnfSeq, hedGaeSeqp, hedDthrAgenda);
		
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error(e.getMessage(), e);
		}
		
		for (AelAmostras amostra : listAmostras) {
			try {
				this.examesFacade.imprimirEtiquetaAmostra(amostra, this.unidadeExecutora, nomeMicrocomputador);
				this.apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_IMPRESSAO_ETIQUETA", amostra.getId()
								.getSeqp());
			} catch (final BaseException e) {
				apresentarExcecaoNegocio(e);
			}
		}
	}
	
	/**
	 * Voltar Coleta
	 */
	public void voltarColeta() {
		this.isAcaoLimpar = false;
		
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error(EXCECAO_CAPTURADA, e);
		}
		
		try {			
			this.examesBeanFacade.atualizarSituacaoExamesAmostraColetada(amostraSelecionada, nomeMicrocomputador);
			// Atualização das Informações dos Exames das Amostras
			this.listaAmostras = this.coletaExamesFacade.buscarAmostrasPorSolicitacaoExame(soeSeqSelecionado);
			this.listaExamesAmostra = this.coletaExamesFacade.obterAmostraItemExamesPorAmostra(this.soeSeqSelecionado, this.seqpSelecionado);
			setDesabilitaBotaoColeta(Boolean.FALSE);
			setDesabilitaBotaoCancelarColeta(Boolean.TRUE);
			this.apresentarMsgNegocio(Severity.INFO,"AMOSTRA_VOLTADA_COM_SUCESSO");
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}
	
	public void selecionarPacienteConsulta() {
		if (this.getPacCodigo() != null) {
				this.paciente = pacienteFacade.obterPacientePorCodigo(this.getPacCodigo());
			} else if (this.getProntuario()!=null) {
				this.paciente = pacienteFacade.obterPacientePorProntuario(this.getProntuario());
		}
		
		if (this.paciente == null) {
			this.apresentarMsgNegocio(Severity.ERROR,"AIP_PACIENTE_NAO_ENCONTRADO");
		}
		else {
			this.setPacCodigo(this.paciente.getCodigo());
			this.setProntuario(this.paciente.getProntuario());
			this.setPacNome(this.paciente.getNome());
		}
	}
	
	public List<AghUnidadesFuncionais> pesquisarUnidadeExecutora(String parametro) {
		if(parametro==null){
			parametro = "";
		}
		return this.aghuFacade.listarUnidadeFuncionalComSala(parametro);
	}
	
	// Redireciona para a Pesquisa Fonética
	public String redirecionarPesquisaFonetica(){
		this.isAcaoLimpar = false;
		return "paciente-pesquisaPacienteComponente";
	}


	public void setColetaExamesFacade(IColetaExamesFacade coletaExamesFacade) {
		this.coletaExamesFacade = coletaExamesFacade;
	}

	public IColetaExamesFacade getColetaExamesFacade() {
		return coletaExamesFacade;
	}

	public IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}

	public void setPacienteFacade(IPacienteFacade pacienteFacade) {
		this.pacienteFacade = pacienteFacade;
	}
	
	public List<AelAmostras> getListaAmostras() {
		return listaAmostras;
	}

	public void setListaAmostras(List<AelAmostras> listaAmostras) {
		this.listaAmostras = listaAmostras;
	}
	
	public Integer getSoeSeqSelecionado() {
		return soeSeqSelecionado;
	}

	public void setSoeSeqSelecionado(Integer soeSeqSelecionado) {
		this.soeSeqSelecionado = soeSeqSelecionado;
	}

	public void setListaExamesAmostra(List<AelExamesAmostraVO> listaExamesAmostra) {
		this.listaExamesAmostra = listaExamesAmostra;
	}

	public List<AelExamesAmostraVO> getListaExamesAmostra() {
		return listaExamesAmostra;
	}

	public void setHedGaeUnfSeq(Short hedGaeUnfSeq) {
		this.hedGaeUnfSeq = hedGaeUnfSeq;
	}

	public Short getHedGaeUnfSeq() {
		return hedGaeUnfSeq;
	}

	public void setHedGaeSeqp(Integer hedGaeSeqp) {
		this.hedGaeSeqp = hedGaeSeqp;
	}

	public Integer getHedGaeSeqp() {
		return hedGaeSeqp;
	}

	public void setHedDthrAgenda(Date hedDthrAgenda) {
		this.hedDthrAgenda = hedDthrAgenda;
	}

	public Date getHedDthrAgenda() {
		return hedDthrAgenda;
	}

	public void setListaSolicitacoes(List<VAelSolicAtendsVO> listaSolicitacoes) {
		this.listaSolicitacoes = listaSolicitacoes;
	}

	public List<VAelSolicAtendsVO> getListaSolicitacoes() {
		return listaSolicitacoes;
	}
	
	public IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

	public void setAghuFacade(IAghuFacade aghuFacade) {
		this.aghuFacade = aghuFacade;
	}

	public IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}

	public void setFaturamentoFacade(IFaturamentoFacade faturamentoFacade) {
		this.faturamentoFacade = faturamentoFacade;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setPossuiCaracteristica(Boolean possuiCaracteristica) {
		this.possuiCaracteristica = possuiCaracteristica;
	}

	public Boolean getPossuiCaracteristica() {
		return possuiCaracteristica;
	}
	
	public IExamesFacade getExamesFacade() {
		return examesFacade;
	}

	public void setExamesFacade(IExamesFacade examesFacade) {
		this.examesFacade = examesFacade;
	}

	public Short getSeqpSelecionado() {
		return seqpSelecionado;
	}

	public void setSeqpSelecionado(Short seqpSelecionado) {
		this.seqpSelecionado = seqpSelecionado;
	}

	public void setDesabilitaBotaoColeta(Boolean desabilitaBotaoColeta) {
		this.desabilitaBotaoColeta = desabilitaBotaoColeta;
	}

	public Boolean getDesabilitaBotaoColeta() {
		return desabilitaBotaoColeta;
	}

	public void setDesabilitaBotaoCancelarColeta(
			Boolean desabilitaBotaoCancelarColeta) {
		this.desabilitaBotaoCancelarColeta = desabilitaBotaoCancelarColeta;
	}

	public Boolean getDesabilitaBotaoCancelarColeta() {
		return desabilitaBotaoCancelarColeta;
	}


	public void setAmostraSelecionada(AelAmostras amostraSelecionada) {
		this.amostraSelecionada = amostraSelecionada;
	}

	public AelAmostras getAmostraSelecionada() {
		return amostraSelecionada;
	}

	public void setSala(AelSalasExecutorasExames sala) {
		this.sala = sala;
	}

	public AelSalasExecutorasExames getSala() {
		return sala;
	}
	

	public AghUnidadesFuncionais getUnidadeExecutora() {
		return unidadeExecutora;
	}

	public void setUnidadeExecutora(AghUnidadesFuncionais unidadeExecutora) {
		this.unidadeExecutora = unidadeExecutora;
	}

	public Integer getSoeSeq() {
		return soeSeq;
	}

	public void setSoeSeq(Integer soeSeq) {
		this.soeSeq = soeSeq;
	}

	public Short getAmostraSeq() {
		return amostraSeq;
	}

	public void setAmostraSeq(Short amostraSeq) {
		this.amostraSeq = amostraSeq;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public String getPacNome() {
		return pacNome;
	}

	public void setPacNome(String pacNome) {
		this.pacNome = pacNome;
	}

	public Integer getPacCodigoFonetica() {
		return pacCodigoFonetica;
	}

	public void setPacCodigoFonetica(Integer pacCodigoFonetica) {
		this.pacCodigoFonetica = pacCodigoFonetica;
	}

	public String getCameFrom() {
		return cameFrom;
	}

	public void setCameFrom(String cameFrom) {
		this.cameFrom = cameFrom;
	}

	public Boolean getOrigemMenu() {
		return origemMenu;
	}

	public void setOrigemMenu(Boolean origemMenu) {
		this.origemMenu = origemMenu;
	}

	public Integer getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(Integer selectedTab) {
		this.selectedTab = selectedTab;
	}

	public Boolean getIsAcaoLimpar() {
		return isAcaoLimpar;
	}

	public void setIsAcaoLimpar(Boolean isAcaoLimpar) {
		this.isAcaoLimpar = isAcaoLimpar;
	}

	public Boolean getPesquisaOk() {
		return pesquisaOk;
	}

	public void setPesquisaOk(Boolean pesquisaOk) {
		this.pesquisaOk = pesquisaOk;
	}

	public Boolean getDesabilitaBotaoColetarExame() {
		return desabilitaBotaoColetarExame;
	}

	public void setDesabilitaBotaoColetarExame(Boolean desabilitaBotaoColetarExame) {
		this.desabilitaBotaoColetarExame = desabilitaBotaoColetarExame;
	}

	public Boolean getDesabilitaBotaoVoltarExame() {
		return desabilitaBotaoVoltarExame;
	}

	public void setDesabilitaBotaoVoltarExame(Boolean desabilitaBotaoVoltarExame) {
		this.desabilitaBotaoVoltarExame = desabilitaBotaoVoltarExame;
	}
}
