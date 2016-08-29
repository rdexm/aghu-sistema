package br.gov.mec.aghu.ambulatorio.action;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;





import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.ambulatorio.vo.FiltroConsultaBloqueioConsultaVO;
import br.gov.mec.aghu.ambulatorio.vo.FiltroParametrosPadraoConsultaVO;
import br.gov.mec.aghu.ambulatorio.vo.SolicitaInterconsultaVO;
import br.gov.mec.aghu.blococirurgico.vo.EquipeVO;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.vo.RapServidoresVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.StringUtil;



public class SolicitarInterconsultaController extends ActionController  {
	
	private static final long serialVersionUID = 1393921256668856337L;

	@EJB  
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB 
	private IParametroFacade parametroFacade;

	private boolean exibirBotaoNovo;
	private FiltroParametrosPadraoConsultaVO filtroPadrao;
	private FiltroConsultaBloqueioConsultaVO filtroConsulta;

	
	private String pacNome;
	private Integer numConsulta;
	private String observacao;	
	private AghEspecialidades aghEspecialidades;
	private EquipeVO aghEquipes;
	private RapServidoresVO rapServidoresVO;
	private List<SolicitaInterconsultaVO> solicitaInterconsultaVO = new ArrayList<SolicitaInterconsultaVO>();
	private SolicitaInterconsultaVO parametroSelecionado;
	private SolicitaInterconsultaVO interconsultaVO;
	private Date dataPrevisao;
	private boolean  exibirBotaoAdicionar = true;
	private boolean  exibirBotaoGravar = true;
	private String teste;

	
	
	
	private static final String PESQUISA_CONSULTA_PACIENTE = "ambulatorio-pesquisaConsultasPaciente";

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void init() {
			if(this.solicitaInterconsultaVO.isEmpty()){
				exibirBotaoGravar = true;
			}
			
			this.observacao = null;
	}

	
	public List<AghEspecialidades> pesquisarAgenda(String paramPesquisa) throws BaseException {
		AacConsultas consulta = new AacConsultas();
		consulta.setCaaSeq(Short.valueOf(this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_CAA_INTERCONSULTA).getVlrNumerico().toString()));
				
		Integer pacCodigo = this.ambulatorioFacade.obterCodigoPacienteOrigem(1 , this.numConsulta);
		Date dtNascimento = this.ambulatorioFacade.obterDataNascimentoAnterior(pacCodigo);
		Integer idadePaciente = DateUtil.getIdade(dtNascimento);
		
		
		return this.returnSGWithCount(this.ambulatorioFacade.pesquisarAgendaInterconsulta(paramPesquisa, consulta,  idadePaciente), this.ambulatorioFacade.pesquisarAgendaInterconsultaCount(paramPesquisa, consulta, idadePaciente));
		
	}
		
	public List<EquipeVO> pesquisarEquipe(String paramPesquisa) throws BaseException {
		return this.returnSGWithCount(this.ambulatorioFacade.pesquisarEquipeInterconsulta(paramPesquisa), this.ambulatorioFacade.pesquisarEquipeInterconsultaCount(paramPesquisa));
	}
	
	public List<RapServidoresVO> pesquisarProfissional(String paramPesquisa) throws BaseException {
		return this.returnSGWithCount(this.ambulatorioFacade.pesquisarServidorInterconsulta(paramPesquisa), this.ambulatorioFacade.pesquisarServidorInterconsultaCount(paramPesquisa));
	}
	
	public void adicionar() throws ApplicationBusinessException {
		this.interconsultaVO = new SolicitaInterconsultaVO();
		Boolean validacao = true;
		validacao = validaCamposRequeridosEmBranco();
		obterDataPrevisaoInterconsulta();
		
		if(validacao){
			if(this.parametroSelecionado != null){
				verificaItemInterconsulta();
			}else{
				obterDadosInterconsulta(this.interconsultaVO);
				if(!this.ambulatorioFacade.verificarInterconsulta(interconsultaVO) && !verificaInterconsultaMemoria(this.interconsultaVO) && !this.ambulatorioFacade.verificarInterconsultaAux(interconsultaVO)){
						if(this.interconsultaVO != null){
							this.solicitaInterconsultaVO.add(interconsultaVO);
							exibirBotaoGravar = false;
							limparPesquisa();
						}
				}else{
					apresentarMsgNegocio(Severity.ERROR, "MAM_01635");
				}
			}
		}
	}
	

	public void verificaItemInterconsulta() {
		for (SolicitaInterconsultaVO interconsulta : solicitaInterconsultaVO) {
			if(interconsulta.equals(parametroSelecionado)){				
					obterDadosInterconsulta(interconsulta);
					interconsulta.setEdita(false);
			}
		}
		limparPesquisa();
		exibirBotaoAdicionar =  true;
	}

	public void obterDadosInterconsulta(SolicitaInterconsultaVO interconsultaVO) {
		
		if(this.aghEquipes != null){
			interconsultaVO.setEquipeSeq(aghEquipes.getSeq());
			interconsultaVO.setEquipeNome(aghEquipes.getNome());
			interconsultaVO.setEquipeMatricula(aghEquipes.getMatricula());
			interconsultaVO.setEquipeVinCodigo(aghEquipes.getVinculo());
		}else{
			this.aghEquipes = null;
			interconsultaVO.setEquipeSeq(null);
			interconsultaVO.setEquipeFormatado(null);
			interconsultaVO.setEquipeNome(null);
			interconsultaVO.setEquipeMatricula(null);
			interconsultaVO.setEquipeVinCodigo(null);
		}
		
		if(this.aghEspecialidades != null){
			interconsultaVO.setEspNome(aghEspecialidades.getNomeEspecialidade());
			interconsultaVO.setEspSeq(aghEspecialidades.getSeq());
			interconsultaVO.setEspSigla(aghEspecialidades.getSigla());
		}
		
		if(this.rapServidoresVO != null){
			interconsultaVO.setRpfCodigo(rapServidoresVO.getRpfCodigo());
			interconsultaVO.setRpfMatricula(rapServidoresVO.getMatricula());
			interconsultaVO.setRpfNome(rapServidoresVO.getRpfNome());
			interconsultaVO.setRpfVinCodigo(rapServidoresVO.getVinculo());
		}else{
			this.rapServidoresVO = null;
			interconsultaVO.setRpfCodigo(null);
			interconsultaVO.setRpfMatricula(null);
			interconsultaVO.setRpfNome(null);
			interconsultaVO.setRpfVinCodigo(null);
			interconsultaVO.setProfissionalFormatado(null);
		}
		
		if(this.dataPrevisao != null){
			interconsultaVO.setPrevisao(this.dataPrevisao);
		}
		
		
		
		interconsultaVO.setCodPaciente(this.ambulatorioFacade.obterCodigoPacienteOrigem(1, this.numConsulta));
		interconsultaVO.setObservacao(StringUtil.trim(this.observacao));
	}

	public void limparPesquisa() {
		
		this.aghEspecialidades =  null;
		this.aghEquipes =  null;
		this.rapServidoresVO =  null;
		this.parametroSelecionado = null;
		this.observacao = null;
		
	}
		
	public String gravar() throws ApplicationBusinessException, ParseException{
		String retorno = null;
		try {
			this.ambulatorioFacade.inserirSolicitacaoInterconsulta(solicitaInterconsultaVO, this.numConsulta);
			this.apresentarMsgNegocio(Severity.INFO, "MSG_SOLICITACAO_INTERCONSULTA_SALVO");
			retorno = cancelar();		
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return retorno;
	}
	
	public void editar(){
		obterDadosParaEditar();
		exibirBotaoAdicionar =  false;
	}
	private void limparEdicao(){
		for(SolicitaInterconsultaVO s : solicitaInterconsultaVO){
			s.setEdita(false);
			
		}
	}
	public void obterDadosParaEditar(){
		limparEdicao();
			
		if(this.parametroSelecionado != null){
			if(parametroSelecionado.getEquipeFormatado() != null){
				this.aghEquipes = new EquipeVO();
				this.aghEquipes.setSeq(parametroSelecionado.getEquipeSeq());
				this.aghEquipes.setNome(parametroSelecionado.getEquipeNome());
				this.aghEquipes.setVinculo(parametroSelecionado.getEquipeVinCodigo());
				this.aghEquipes.setMatricula(parametroSelecionado.getEquipeMatricula());
			}else{
				this.aghEquipes  = null;
			}
			
			if(parametroSelecionado.getAgendaFormatado() != null){
				this.aghEspecialidades = new AghEspecialidades();
				this.aghEspecialidades.setNomeEspecialidade(parametroSelecionado.getEspNome());
				this.aghEspecialidades.setSeq(parametroSelecionado.getEspSeq());
				this.aghEspecialidades.setSigla(parametroSelecionado.getEspSigla());
			}
			
			if(parametroSelecionado.getProfissionalFormatado() != null){
				this.rapServidoresVO = new RapServidoresVO();
				this.rapServidoresVO.setRpfCodigo(parametroSelecionado.getRpfCodigo());
				this.rapServidoresVO.setRpfNome(parametroSelecionado.getRpfNome());
				this.rapServidoresVO.setVinculo(parametroSelecionado.getRpfVinCodigo());
				this.rapServidoresVO.setMatricula(parametroSelecionado.getRpfMatricula());
				
			}else{
				this.rapServidoresVO = null;
			}
			this.parametroSelecionado.setEdita(true);
			this.setObservacao(parametroSelecionado.getObservacao());
		}
	}
	
	
	public void excluir(){
		if(this.parametroSelecionado != null){
			this.solicitaInterconsultaVO.remove(this.parametroSelecionado);			
		}
		limparPesquisa();
		exibirBotaoAdicionar =  true;
		if(this.solicitaInterconsultaVO.isEmpty()){
			exibirBotaoGravar = true;
		}
	}
	
	public String cancelar(){
		limparPesquisa();
		exibirBotaoAdicionar =  true;
		this.solicitaInterconsultaVO = new ArrayList<SolicitaInterconsultaVO>();
		return PESQUISA_CONSULTA_PACIENTE;
	}
	
	public void cancelarEdicao(){
		this.setObservacao(null);
		limparPesquisa();
		limparEdicao();
		exibirBotaoAdicionar =  true;
				
	}
	
	public Date obterDataPrevisaoInterconsulta() throws ApplicationBusinessException{
		AacConsultas consulta = this.ambulatorioFacade.obterConsultaPorNumero(this.numConsulta);
		if(this.aghEspecialidades != null){
			this.dataPrevisao = this.ambulatorioFacade.obterDtPrevisaoInterconsulta(aghEspecialidades.getSeq(), consulta.getCaaSeq());
		}
		return this.dataPrevisao;
	}
	
	private boolean validaCamposRequeridosEmBranco(){
		boolean retorno = true;
		boolean retorno1 = true;
		boolean retorno2 = true;
		
		
		if(this.aghEspecialidades != null){
			retorno1 = true;
		}else{
			this.aghEspecialidades = new AghEspecialidades();
			retorno1 = false;
			this.aghEspecialidades = null;
			this.apresentarMsgNegocio(Severity.ERROR, "CAMPO_OBRIGATORIO", "Agenda");
			
		}
		if(this.observacao != null){
			retorno2 = true;
		}else {
			retorno2 = false;
			this.observacao = null;
			this.apresentarMsgNegocio(Severity.ERROR, "CAMPO_OBRIGATORIO", "Observação");
		}
		
		if(this.observacao != null && this.observacao.trim().length() == 0){
			retorno2 = false;
			this.observacao = null;
			this.apresentarMsgNegocio(Severity.ERROR, "CAMPO_OBRIGATORIO", "Observação");
		}
		
		if(retorno1 && retorno2){
			retorno = true;
		}else{
			retorno = false;
		}
		
		return retorno;
	}
	
	public Boolean verificaInterconsultaMemoria(SolicitaInterconsultaVO interconsultaVO){
		Boolean retorno = false;
		if(!this.solicitaInterconsultaVO.isEmpty()){
			for (SolicitaInterconsultaVO solicitaInterconsultaVO2 : solicitaInterconsultaVO) {
				if(solicitaInterconsultaVO2.getEspSeq().equals(interconsultaVO.getEspSeq())){
					retorno = true;
				}
			}
		}
		return retorno;
	}
	

	/************************* getters and setters ***************/

	public boolean isExibirBotaoNovo() {
		return exibirBotaoNovo;
	}

	public void setExibirBotaoNovo(boolean exibirBotaoNovo) {
		this.exibirBotaoNovo = exibirBotaoNovo;
	}

	public FiltroParametrosPadraoConsultaVO getFiltroPadrao() {
		return filtroPadrao;
	}

	public void setFiltroPadrao(FiltroParametrosPadraoConsultaVO filtroPadrao) {
		this.filtroPadrao = filtroPadrao;
	}

	public FiltroConsultaBloqueioConsultaVO getFiltroConsulta() {
		return filtroConsulta;
	}

	public void setFiltroConsulta(FiltroConsultaBloqueioConsultaVO filtroConsulta) {
		this.filtroConsulta = filtroConsulta;
	}

	public Integer getNumConsulta() {
		return numConsulta;
	}

	public void setNumConsulta(Integer numConsulta) {
		this.numConsulta = numConsulta;
	}

	public String getPacNome() {
		return pacNome;
	}

	public void setPacNome(String pacNome) {
		this.pacNome = pacNome;
	}

	public EquipeVO getAghEquipes() {
		return aghEquipes;
	}

	public void setAghEquipes(EquipeVO aghEquipes) {
		this.aghEquipes = aghEquipes;
	}

	public RapServidoresVO getRapServidoresVO() {
		return rapServidoresVO;
	}

	public void setRapServidoresVO(RapServidoresVO rapServidoresVO) {
		this.rapServidoresVO = rapServidoresVO;
	}

	public AghEspecialidades getAghEspecialidades() {
		return aghEspecialidades;
	}

	public void setAghEspecialidades(AghEspecialidades aghEspecialidades) {
		this.aghEspecialidades = aghEspecialidades;
	}

	public String getObservacao() {
		String descObs = "Telefone: ";
		if(observacao != null && observacao.length() > 10){
			descObs = "";
		}else{
			observacao = descObs;
		}
		return this.observacao.trim();
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public List<SolicitaInterconsultaVO> getSolicitaInterconsultaVO() {
		return solicitaInterconsultaVO;
	}

	public void setSolicitaInterconsultaVO(
			List<SolicitaInterconsultaVO> solicitaInterconsultaVO) {
		this.solicitaInterconsultaVO = solicitaInterconsultaVO;
	}

	public SolicitaInterconsultaVO getParametroSelecionado() {
		return parametroSelecionado;
	}

	public void setParametroSelecionado(SolicitaInterconsultaVO parametroSelecionado) {
		this.parametroSelecionado = parametroSelecionado;
	}

	public Date getDataPrevisao() {
		return dataPrevisao;
	}

	public void setDataPrevisao(Date dataPrevisao) {
		this.dataPrevisao = dataPrevisao;
	}

	public SolicitaInterconsultaVO getInterconsultaVO() {
		return interconsultaVO;
	}

	public void setInterconsultaVO(SolicitaInterconsultaVO interconsultaVO) {
		this.interconsultaVO = interconsultaVO;
	}

	public boolean isExibirBotaoAdicionar() {
		return exibirBotaoAdicionar;
	}

	public void setExibirBotaoAdicionar(boolean exibirBotaoAdicionar) {
		this.exibirBotaoAdicionar = exibirBotaoAdicionar;
	}

	public boolean isExibirBotaoGravar() {
		return exibirBotaoGravar;
	}

	public void setExibirBotaoGravar(boolean exibirBotaoGravar) {
		this.exibirBotaoGravar = exibirBotaoGravar;
	}

	public String getTeste() {
		return teste;
	}

	public void setTeste(String teste) {
		this.teste = teste;
	}
					
}
