package br.gov.mec.aghu.exames.coleta.action;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;

import br.gov.mec.aghu.business.bancosangue.IBancoDeSangueFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSubTipoImpressaoLaudo;
import br.gov.mec.aghu.dominio.DominioTipoImpressaoLaudo;
import br.gov.mec.aghu.exames.action.ConsultarResultadosNotaAdicionalController;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.coleta.business.IColetaExamesFacade;
import br.gov.mec.aghu.exames.coleta.vo.PesquisaSolicitacaoDiversosFiltroVO;
import br.gov.mec.aghu.exames.patologia.business.IExamesPatologiaFacade;
import br.gov.mec.aghu.exames.pesquisa.action.PesquisaExamesPorPacienteController;
import br.gov.mec.aghu.exames.pesquisa.business.IPesquisaExamesFacade;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaExamesPacientesResultsVO;
import br.gov.mec.aghu.exames.pesquisa.vo.VAelExamesAtdDiversosVO;
import br.gov.mec.aghu.exames.questionario.business.IQuestionarioExamesFacade;
import br.gov.mec.aghu.exames.solicitacao.action.DetalharItemSolicitacaoExameController;
import br.gov.mec.aghu.model.AbsCandidatosDoadores;
import br.gov.mec.aghu.model.AelCadCtrlQualidades;
import br.gov.mec.aghu.model.AelConfigExLaudoUnico;
import br.gov.mec.aghu.model.AelDadosCadaveres;
import br.gov.mec.aghu.model.AelLaboratorioExternos;
import br.gov.mec.aghu.model.AelProjetoPesquisas;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;

/**
 * Controller para tela de pesquisa de solicitação de diversos
 * 
 * @author fpalma
 *
 */


public class PesquisaSolicitacaoDiversosController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	public enum PesquisaSolicitacaoDiversosControllerONExceptionCode implements BusinessExceptionCode {
		MSG_INFORME_DATA_INICIAL_FINAL
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8746156604747346393L;

	@EJB
	private IColetaExamesFacade coletaExamesFacade;
	
	@EJB
	private IQuestionarioExamesFacade questionarioExamesFacade;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IBancoDeSangueFacade bancoDeSangueFacade;
	
	@EJB
	private IPesquisaExamesFacade pesquisaExamesFacade;

	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;
	
	@Inject
	private ConsultarResultadosNotaAdicionalController consultarResultadosNotaAdicionalController;
	
	@Inject
	private PesquisaExamesPorPacienteController pesquisaExamesPorPacienteController;
	
	@Inject
	private DetalharItemSolicitacaoExameController detalharItemSolicitacaoExameController;
	
	//Sugestions
	private AelProjetoPesquisas projetoPesquisa;
	private AelLaboratorioExternos laboratorioExterno;
	private AelCadCtrlQualidades controleQualidade;
	private AelDadosCadaveres cadaver;
	private AbsCandidatosDoadores doador;
	private AelSitItemSolicitacoes situacao;
	
	private Boolean desabilitaProjetoPesquisa;
	private Boolean desabilitaLaboratorioExterno;
	private Boolean desabilitaControleQualidade;
	private Boolean desabilitaCadaver;
	private Boolean desabilitaDoador;
	

	private Integer codPac;
	private Integer prontPac;
	private AipPacientes paciente;
	private Integer solicitacao;
	private Long numeroAp;
	private AelConfigExLaudoUnico configExLaudoUnico;
	private Integer lu2Seq;
	
	private DominioSimNao mostraExamesCancelados;
	private DominioSimNao somenteLaboratorial;
	private DominioSimNao impressoLaudo;
	
	private Date dtInicio;
	private Date dtFinal;
	
	private Integer seqSelecionado;

	private List<VAelExamesAtdDiversosVO> listaSolicitacaoDiversos;
	private PesquisaSolicitacaoDiversosFiltroVO filtro;
	
	// Flag para indicar se já foi feita uma pesquisa
	private Boolean pesquisaEfetuada;
	
	private Map<Integer, Vector<Short>> solicitacoes = new HashMap<Integer, Vector<Short>>();
	
	private Integer codigoSoeSelecionado;
	private Short iseSeqSelecionado;
	
	private String voltarPara;
	
	public void pesquisaPaciente(ValueChangeEvent event){
		try {
			paciente = pacienteFacade.pesquisarPacienteComponente(event.getNewValue(), event.getComponent().getId());
		}catch(BaseException e){
			apresentarExcecaoNegocio(e);
		}
	}
	
	public List<AelProjetoPesquisas> obterProjetosPesquisa(String parametro) {
		return questionarioExamesFacade.pesquisarProjetosPesquisaPorNumeroOuNome((String) parametro);
	}
	
	public List<AelLaboratorioExternos> obterLaboratoriosExternos(String parametro) {
		return examesFacade.obterLaboratorioExternoList((String) parametro);
	}
	
	public List<AelCadCtrlQualidades> obterControlesQualidade(String parametro) {
		return questionarioExamesFacade.obterCadCtrlQualidadesList((String) parametro);
	}
	
	public List<AelDadosCadaveres> obterCadaveres(String parametro) {
		return questionarioExamesFacade.obterDadosCadaveresList((String) parametro);
	}
	
	public List<AbsCandidatosDoadores> obterDoadores(String parametro) {
		return bancoDeSangueFacade.obterCandidatosDoadoresList((String) parametro);
	}
	
	public List<AelSitItemSolicitacoes> obterSituacoes(String parametro) {
		return coletaExamesFacade.pesquisarSitItemSolicitacoesPorCodigoOuDescricao((String) parametro);
	}
	
	/**
	 * Metodo que limpa os campos de filtro<br>
	 * na tela de pesquisa.
	 */
	public void limparPesquisa() {
		this.habilitarSuggestions();
		this.paciente = null;
		this.prontPac = null;
		this.codPac = null;
		this.solicitacao = null;
		this.numeroAp = null;
		this.situacao = null;
		this.pesquisaEfetuada = false;
		this.listaSolicitacaoDiversos = null;
		this.seqSelecionado = null;
		this.projetoPesquisa = null;
		this.laboratorioExterno = null;
		this.controleQualidade = null;
		this.cadaver = null;
		this.doador = null;
		this.situacao = null;
		this.mostraExamesCancelados = DominioSimNao.N;
		this.somenteLaboratorial = DominioSimNao.N;
		this.impressoLaudo = null;
		this.filtro = null;
		this.solicitacoes = new HashMap<Integer, Vector<Short>>();
	}
	
	public void inicio() {
	 

		this.pesquisaEfetuada = false;
		this.mostraExamesCancelados = DominioSimNao.N;
		this.somenteLaboratorial = DominioSimNao.N;

		if (lu2Seq != null) {
			configExLaudoUnico = examesPatologiaFacade.obterConfigExameLaudoUncioPorChavePrimaria(lu2Seq);
		}
		
		if (this.codPac != null) {
			paciente = pacienteFacade.obterPaciente(this.codPac);
			prontPac = paciente.getProntuario();
		}
	
	}
	
	public void pesquisar(){
		try {
			if (solicitacao == null && configExLaudoUnico == null && numeroAp == null && paciente == null) {
				throw new BaseException(PesquisaSolicitacaoDiversosControllerONExceptionCode.MSG_INFORME_DATA_INICIAL_FINAL);
			}
			this.solicitacoes = new HashMap<Integer, Vector<Short>>();
			popularFiltroPesquisa();
			
			listaSolicitacaoDiversos = coletaExamesFacade.pesquisarSolicitacaoDiversos(this.filtro);
			this.pesquisaEfetuada = true;
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}
	
	public void desabilitarSuggestions() {
		if(this.projetoPesquisa != null) {
			this.desabilitaLaboratorioExterno = true;
			this.desabilitaControleQualidade = true;
			this.desabilitaCadaver = true;
			this.desabilitaDoador = true;
		}
		else if(this.laboratorioExterno != null) {
			this.desabilitaProjetoPesquisa = true;
			this.desabilitaControleQualidade = true;
			this.desabilitaCadaver = true;
			this.desabilitaDoador = true;
		}
		else if(this.controleQualidade != null) {
			this.desabilitaProjetoPesquisa = true;
			this.desabilitaLaboratorioExterno = true;
			this.desabilitaCadaver = true;
			this.desabilitaDoador = true;
		}
		else if(this.cadaver != null) {
			this.desabilitaProjetoPesquisa = true;
			this.desabilitaLaboratorioExterno = true;
			this.desabilitaControleQualidade = true;
			this.desabilitaDoador = true;
		}
		else if(this.doador != null) {
			this.desabilitaProjetoPesquisa = true;
			this.desabilitaLaboratorioExterno = true;
			this.desabilitaControleQualidade = true;
			this.desabilitaCadaver = true;
		}
		
	}
	
	public void habilitarSuggestions() {
		this.desabilitaProjetoPesquisa = false;
		this.desabilitaLaboratorioExterno = false;
		this.desabilitaControleQualidade = false;
		this.desabilitaCadaver = false;
		this.desabilitaDoador = false;
	}
	
	private void popularFiltroPesquisa() {
		this.filtro = new PesquisaSolicitacaoDiversosFiltroVO();
		this.filtro.setProjetoPesquisa(projetoPesquisa);
		this.filtro.setLaboratorioExterno(laboratorioExterno);
		this.filtro.setControleQualidade(controleQualidade);
		this.filtro.setCadaver(cadaver);
		this.filtro.setDoador(doador);
		if(paciente != null){
			this.filtro.setCodPaciente(paciente.getCodigo());
		}
		this.filtro.setSolicitacao(solicitacao);
		this.filtro.setNumeroAp(numeroAp);
		this.filtro.setConfigExLaudoUnico(configExLaudoUnico);
		this.filtro.setSituacao(situacao);
		this.filtro.setMostraExamesCancelados(mostraExamesCancelados);
		this.filtro.setSomenteLaboratorial(somenteLaboratorial);
		this.filtro.setImpressoLaudo(impressoLaudo);
		this.filtro.setDtInicio(this.dtInicio);
		this.filtro.setDtFinal(this.dtFinal);
	}
	
	public void selecionaItemExame(Integer codigoSoeSelecionado, Short iseSeqSelecionado) {
		if(this.solicitacoes.containsKey(codigoSoeSelecionado)){
			if(this.solicitacoes.get(codigoSoeSelecionado).contains(iseSeqSelecionado)){

				this.solicitacoes.get(codigoSoeSelecionado).remove(iseSeqSelecionado);

				if(this.solicitacoes.get(codigoSoeSelecionado).size()==0){
					this.solicitacoes.remove(codigoSoeSelecionado);
				}
			}else{
				this.solicitacoes.get(codigoSoeSelecionado).add(iseSeqSelecionado);
			}
		}else{
			this.solicitacoes.put(codigoSoeSelecionado, new Vector<Short>());
			this.solicitacoes.get(codigoSoeSelecionado).add(iseSeqSelecionado);
		}
	}
	
	public String verResultados() {
		
		if (this.solicitacoes != null && this.solicitacoes.size() > 0) {
			
			Map<Integer, Vector<Short>> solicitacoesTemp = new HashMap<Integer, Vector<Short>>();

			try{
				/*Verifica a situação dos itens*/
				this.pesquisaExamesFacade.validaSituacaoExamesSelecionados(solicitacoes, Boolean.FALSE, Boolean.TRUE);

				if(this.pesquisaExamesFacade.permiteVisualizarLaudoMedico()){
					consultarResultadosNotaAdicionalController.setSolicitacoes(solicitacoes);
					consultarResultadosNotaAdicionalController.setTipoLaudo(DominioTipoImpressaoLaudo.LAUDO_SAMIS);
					return "exames-consultarResultadoNotaAdicional";

				}else if(!this.pesquisaExamesFacade.permiteVisualizarLaudoMedico() && this.pesquisaExamesFacade.permitevisualizarLaudoAtdExt()){
					List<PesquisaExamesPacientesResultsVO> listaPacientes = coletaExamesFacade.popularListaImpressaoLaudo(this.listaSolicitacaoDiversos);
					solicitacoesTemp = this.pesquisaExamesFacade.obterListaSolicitacoesImpressaoLaudo(solicitacoes, listaPacientes, pesquisaExamesPorPacienteController.getProntuario(), DominioTipoImpressaoLaudo.LAUDO_PAC_EXTERNO);

					consultarResultadosNotaAdicionalController.setTipoLaudo(DominioTipoImpressaoLaudo.LAUDO_PAC_EXTERNO);

				}else if(!this.pesquisaExamesFacade.permiteVisualizarLaudoMedico() && !this.pesquisaExamesFacade.permitevisualizarLaudoAtdExt()){
					List<PesquisaExamesPacientesResultsVO> listaPacientes = coletaExamesFacade.popularListaImpressaoLaudo(this.listaSolicitacaoDiversos);
					solicitacoesTemp = this.pesquisaExamesFacade.obterListaSolicitacoesImpressaoLaudo(solicitacoes, listaPacientes, pesquisaExamesPorPacienteController.getProntuario(), DominioTipoImpressaoLaudo.LAUDO_SAMIS);

					consultarResultadosNotaAdicionalController.setTipoLaudo(DominioTipoImpressaoLaudo.LAUDO_SAMIS);

				}

				consultarResultadosNotaAdicionalController.setSolicitacoes(solicitacoesTemp);
				consultarResultadosNotaAdicionalController.setDominioSubTipoImpressaoLaudo(DominioSubTipoImpressaoLaudo.LAUDO_GERAL);
				consultarResultadosNotaAdicionalController.directPrint(null);
				
			}catch (BaseException e) {
				apresentarExcecaoNegocio(e);
				return null;
			}
		}
		return null;
	}
	
	
	public String getUrlImpax() {
		String url = null;
		try {
			url = pesquisaExamesFacade.getUrlImpax(solicitacoes);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return url;
	}
	
	public String redirecionarRespostaQuestionario() {
		if (this.solicitacoes != null && !this.solicitacoes.isEmpty()) {
			try{
				if (!this.pesquisaExamesFacade.validaQuantidadeExamesSelecionados(solicitacoes)) {
					this.apresentarMsgNegocio(Severity.ERROR,"ERRO_SELECIONE_APENAS_UMA_SOLICITACAO_EXAME");
					return "";
				}
				Iterator it = ((java.util.HashMap<Integer, Vector<Short>>)solicitacoes).entrySet().iterator();

				if (it.hasNext()) {
					Map.Entry<Integer, Vector<Short>> paramCLValores = (Map.Entry<Integer, Vector<Short>>)it.next();

					/*Item de solicitacao*/
					Integer solicitacao = paramCLValores.getKey();

					/*coleção de seqp da solicitacao acima*/
					Vector<Short> seqps = paramCLValores.getValue();

					selecionarItemExame(solicitacao, seqps.get(0));
				}
				this.pesquisaExamesFacade.validarExamesComResposta(this.codigoSoeSelecionado, this.iseSeqSelecionado);
				return "respostaQuestionario";
			} catch(ApplicationBusinessException e){
				this.apresentarMsgNegocio(Severity.ERROR,"ERRO_SELECIONE_UMA_SOLICITACAO_COM_RESPOSTA_QUESTIONARIO");
				return "";
			}
		}
		return "";
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String detalhesExames() {

		if (this.solicitacoes != null && !this.solicitacoes.isEmpty()) {
			if (!this.pesquisaExamesFacade.validaQuantidadeExamesSelecionados(solicitacoes)) {
				this.apresentarMsgNegocio(Severity.ERROR,"ERRO_SELECIONE_APENAS_UMA_SOLICITACAO_EXAME");
				return null;
			}

			Iterator it = ((java.util.HashMap<Integer, Vector<Short>>)solicitacoes).entrySet().iterator();

			if (it.hasNext()) {

				Map.Entry<Integer, Vector<Short>> paramCLValores = (Map.Entry<Integer, Vector<Short>>)it.next();

				/*Item de solicitacao*/
				Integer solicitacao = paramCLValores.getKey();

				/*coleção de seqp da solicitacao acima*/
				Vector<Short> seqps = paramCLValores.getValue();

				selecionarItemExame(solicitacao, seqps.get(0));
			}
		}
		
		detalharItemSolicitacaoExameController.setSoeSeq(codigoSoeSelecionado);
		detalharItemSolicitacaoExameController.setSeqp(iseSeqSelecionado);
		detalharItemSolicitacaoExameController.setVoltarPara("exames-pesquisarSolicitacaoDiversos");
		
		return "exames-detalhesItemExames";
	}
	
	public String voltar() {
		return voltarPara;
	}
	
	public String redirecionarPesquisaFonetica(){
		return "paciente-pesquisaPacienteComponente";
	}
	
	private void selecionarItemExame(Integer codigoSoeSelecionado, Short iseSeqSelecionado) {
		setCodigoSoeSelecionado(codigoSoeSelecionado);
		setIseSeqSelecionado(iseSeqSelecionado);
	}
	
	
	public DominioSimNao[] getDominioSimNao() {
		DominioSimNao[] dom = {DominioSimNao.N, DominioSimNao.S};
		return dom;
	}

	public AelProjetoPesquisas getProjetoPesquisa() {
		return projetoPesquisa;
	}

	public void setProjetoPesquisa(AelProjetoPesquisas projetoPesquisa) {
		this.projetoPesquisa = projetoPesquisa;
	}

	public AelLaboratorioExternos getLaboratorioExterno() {
		return laboratorioExterno;
	}

	public void setLaboratorioExterno(AelLaboratorioExternos laboratorioExterno) {
		this.laboratorioExterno = laboratorioExterno;
	}

	public AelCadCtrlQualidades getControleQualidade() {
		return controleQualidade;
	}

	public void setControleQualidade(AelCadCtrlQualidades controleQualidade) {
		this.controleQualidade = controleQualidade;
	}

	public AelDadosCadaveres getCadaver() {
		return cadaver;
	}

	public void setCadaver(AelDadosCadaveres cadaver) {
		this.cadaver = cadaver;
	}

	public AbsCandidatosDoadores getDoador() {
		return doador;
	}

	public void setDoador(AbsCandidatosDoadores doador) {
		this.doador = doador;
	}

	public AelSitItemSolicitacoes getSituacao() {
		return situacao;
	}

	public void setSituacao(AelSitItemSolicitacoes situacao) {
		this.situacao = situacao;
	}

	public Integer getSolicitacao() {
		return solicitacao;
	}

	public void setSolicitacao(Integer solicitacao) {
		this.solicitacao = solicitacao;
	}

	public Long getNumeroAp() {
		return numeroAp;
	}

	public void setNumeroAp(Long numeroAp) {
		this.numeroAp = numeroAp;
	}

	public DominioSimNao getMostraExamesCancelados() {
		return mostraExamesCancelados;
	}

	public void setMostraExamesCancelados(DominioSimNao mostraExamesCancelados) {
		this.mostraExamesCancelados = mostraExamesCancelados;
	}

	public DominioSimNao getSomenteLaboratorial() {
		return somenteLaboratorial;
	}

	public void setSomenteLaboratorial(DominioSimNao somenteLaboratorial) {
		this.somenteLaboratorial = somenteLaboratorial;
	}

	public DominioSimNao getImpressoLaudo() {
		return impressoLaudo;
	}

	public void setImpressoLaudo(DominioSimNao impressoLaudo) {
		this.impressoLaudo = impressoLaudo;
	}

	public Integer getSeqSelecionado() {
		return seqSelecionado;
	}

	public void setSeqSelecionado(Integer seqSelecionado) {
		this.seqSelecionado = seqSelecionado;
	}

	public Boolean getPesquisaEfetuada() {
		return pesquisaEfetuada;
	}

	public void setPesquisaEfetuada(Boolean pesquisaEfetuada) {
		this.pesquisaEfetuada = pesquisaEfetuada;
	}

	public List<VAelExamesAtdDiversosVO> getListaSolicitacaoDiversos() {
		return listaSolicitacaoDiversos;
	}

	public void setListaSolicitacaoDiversos(
			List<VAelExamesAtdDiversosVO> listaSolicitacaoDiversos) {
		this.listaSolicitacaoDiversos = listaSolicitacaoDiversos;
	}

	public PesquisaSolicitacaoDiversosFiltroVO getFiltro() {
		return filtro;
	}

	public void setFiltro(PesquisaSolicitacaoDiversosFiltroVO filtro) {
		this.filtro = filtro;
	}
	
	public List<AelConfigExLaudoUnico> pesquisarAelConfigExLaudoUnico(String value){
		return this.returnSGWithCount(examesPatologiaFacade.pesquisarAelConfigExLaudoUnico(AelConfigExLaudoUnico.Fields.NOME.toString(), (String) value),pesquisarAelConfigExLaudoUnicoCount(value));
	}
	
	public Long pesquisarAelConfigExLaudoUnicoCount(String value){
		return examesPatologiaFacade.pesquisarAelConfigExLaudoUnicoCount(AelConfigExLaudoUnico.Fields.NOME.toString(), (String) value);
	}
	
	public Map<Integer, Vector<Short>> getSolicitacoes() {
		return solicitacoes;
	}

	public void setSolicitacoes(Map<Integer, Vector<Short>> solicitacoes) {
		this.solicitacoes = solicitacoes;
	}

	public Boolean getDesabilitaProjetoPesquisa() {
		return desabilitaProjetoPesquisa;
	}

	public void setDesabilitaProjetoPesquisa(Boolean desabilitaProjetoPesquisa) {
		this.desabilitaProjetoPesquisa = desabilitaProjetoPesquisa;
	}

	public Boolean getDesabilitaLaboratorioExterno() {
		return desabilitaLaboratorioExterno;
	}

	public void setDesabilitaLaboratorioExterno(Boolean desabilitaLaboratorioExterno) {
		this.desabilitaLaboratorioExterno = desabilitaLaboratorioExterno;
	}

	public Boolean getDesabilitaControleQualidade() {
		return desabilitaControleQualidade;
	}

	public void setDesabilitaControleQualidade(Boolean desabilitaControleQualidade) {
		this.desabilitaControleQualidade = desabilitaControleQualidade;
	}

	public Boolean getDesabilitaCadaver() {
		return desabilitaCadaver;
	}

	public void setDesabilitaCadaver(Boolean desabilitaCadaver) {
		this.desabilitaCadaver = desabilitaCadaver;
	}

	public Boolean getDesabilitaDoador() {
		return desabilitaDoador;
	}

	public void setDesabilitaDoador(Boolean desabilitaDoador) {
		this.desabilitaDoador = desabilitaDoador;
	}

	public Integer getCodigoSoeSelecionado() {
		return codigoSoeSelecionado;
	}

	public void setCodigoSoeSelecionado(Integer codigoSoeSelecionado) {
		this.codigoSoeSelecionado = codigoSoeSelecionado;
	}

	public Short getIseSeqSelecionado() {
		return iseSeqSelecionado;
	}

	public void setIseSeqSelecionado(Short iseSeqSelecionado) {
		this.iseSeqSelecionado = iseSeqSelecionado;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public void setDtInicio(Date dtInicio) {
		this.dtInicio = dtInicio;
	}

	public Date getDtInicio() {
		return dtInicio;
	}

	public void setDtFinal(Date dtFinal) {
		this.dtFinal = dtFinal;
	}

	public Date getDtFinal() {
		return dtFinal;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public Integer getCodPac() {
		return codPac;
	}

	public void setCodPac(Integer codPac) {
		this.codPac = codPac;
	}

	public Integer getProntPac() {
		return prontPac;
	}

	public void setProntPac(Integer prontPac) {
		this.prontPac = prontPac;
	}

	public AelConfigExLaudoUnico getConfigExLaudoUnico() {
		return configExLaudoUnico;
	}

	public void setConfigExLaudoUnico(AelConfigExLaudoUnico configExLaudoUnico) {
		this.configExLaudoUnico = configExLaudoUnico;
	}

	public Integer getLu2Seq() {
		return lu2Seq;
	}

	public void setLu2Seq(Integer lu2Seq) {
		this.lu2Seq = lu2Seq;
	}

}
