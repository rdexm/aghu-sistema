package br.gov.mec.aghu.procedimentoterapeutico.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MptFavoritoServidor;
import br.gov.mec.aghu.model.MptPrescricaoPaciente;
import br.gov.mec.aghu.model.MptTipoSessao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.action.PesquisaPacienteController;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade;
import br.gov.mec.aghu.procedimentoterapeutico.vo.PacientesTratamentoSessaoVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;

public class PesquisarPacienteSessoesTerapeuticasPaginatorController extends ActionController implements ActionPaginator{
	
	private static final long serialVersionUID = -8771566611209203668L;
	
	@Inject @Paginator
	private DynamicDataModel<MptPrescricaoPaciente> dataModel;

	@EJB
	private IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade;
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private PesquisaPacienteController pesquisaPacienteController;
	
	private static final String PESQUISA_FONETICA = "paciente-pesquisaPacienteComponente";
	
		
	private AipPacientes paciente;	
	
	//Filtros para a consulta
	private MptTipoSessao tipoSessao;
	private MptTipoSessao tipoSessaoUsuario;
	private Date periodoInicial;
	private Date periodoFinal;
	private Boolean checkAberto;
	private Boolean checkFechado;
	private Boolean checkPrimeiro;
	private Boolean checkSessao;
	private Integer tipoFiltroPersonalizado;	
	private Integer codigoPaciente;
	private RapServidores servidorLogado;
	private MptFavoritoServidor favoritosServidor;
	private List<PacientesTratamentoSessaoVO> listaGrid;
	private List<PacientesTratamentoSessaoVO> listaGridRetorno;

	@PostConstruct
	protected void inicializar() {
	 this.begin(conversation);
	 tipoFiltroPersonalizado = 1;
	 servidorLogado = servidorLogadoFacade.obterServidorLogadoSemCache();
	 favoritosServidor = procedimentoTerapeuticoFacade.pesquisarTipoSessaoUsuario(servidorLogado.getId().getMatricula(), servidorLogado.getId().getVinCodigo());
	 if(favoritosServidor != null){
		 tipoSessaoUsuario = procedimentoTerapeuticoFacade.pesquisarTipoSessaaoFavorito(favoritosServidor.getTipoSessao());
		 tipoSessao = tipoSessaoUsuario;
	 }
	 checkAberto = true;
	 checkPrimeiro = true;
	 checkSessao = true;
	}
	
	public String obterHint(String item, Integer tamanhoMaximo) {
		String itemCapitalizado = WordUtils.capitalizeFully(item);
		if (itemCapitalizado.length() > tamanhoMaximo) {
			itemCapitalizado = StringUtils.abbreviate(itemCapitalizado, tamanhoMaximo);
		}
			
		return itemCapitalizado;
	}
	
	public Long pesquisarAgendamento(Integer lote, Integer consulta){
		return procedimentoTerapeuticoFacade.pesquisarAgendmento(lote,consulta);
	}
	
	public String nomeResponsavel(String nome1, String nome2){
		return procedimentoTerapeuticoFacade.nomeResponsavel(nome1,nome2);
	}
	
	public String pesquisarProtocoloGrid(Integer cloSeq){
		if(cloSeq == null){
			cloSeq = 0;
		}
		return procedimentoTerapeuticoFacade.pesquisarProtocoloGrid(cloSeq);
	}
	
	public void pesquisaPaciente(ValueChangeEvent event){
		try {
			paciente = pacienteFacade.pesquisarPacienteComponente(event.getNewValue(), event.getComponent().getId());
		}catch(BaseException e){
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String redirecionarPesquisaFonetica() {
		pesquisaPacienteController.setTipoSessao(tipoSessao);
		pesquisaPacienteController.setPeriodoInicial(periodoInicial);
		pesquisaPacienteController.setPeriodoFinal(periodoFinal);
		pesquisaPacienteController.setCheckAberto(checkAberto);
		pesquisaPacienteController.setCheckFechado(checkFechado);
		pesquisaPacienteController.setCheckPrimeiro(checkPrimeiro);
		pesquisaPacienteController.setCheckSessao(checkSessao);
		pesquisaPacienteController.setTipoFiltroPersonalizado(tipoFiltroPersonalizado);
		return PESQUISA_FONETICA;
	}
	
	public List<MptTipoSessao> listarTipoSessao(){
		return procedimentoTerapeuticoFacade.listarTiposSessaoCombo();
	}	
	
	public void limpar(){
		if(tipoSessaoUsuario != null){
			tipoSessao = tipoSessaoUsuario;
		}else{
			tipoSessao = null;			
		}
		periodoInicial = null;
		periodoFinal = null;
		checkAberto = true;
		checkFechado = false;
		checkPrimeiro = true;
		checkSessao = true;
		paciente = null;
		tipoFiltroPersonalizado = 1;
		dataModel.limparPesquisa();
	}
	
	public void pesquisar(){
		setCodigoPaciente(0);
		if(paciente != null){
			setCodigoPaciente(paciente.getCodigo());
		}		
		listaGrid = null;
		listaGridRetorno = null;
		dataModel.reiniciarPaginator();
	}
	
	
	public void processarBuscaPacientePorCodigo(Integer codigoPaciente){
		if(codigoPaciente != null){
			this.setPaciente(this.pacienteFacade.buscaPaciente(codigoPaciente));
		}else{
			this.paciente = null;
		}
	}

	@Override
	public Long recuperarCount() {
		Integer retorno = 0;
		Boolean validacao = false;
		validacao = validarCamposObrigatorios(validacao);
		
		if(!validacao){
			try {
				retorno = procedimentoTerapeuticoFacade.pesquisarPacientesTratamentoSessaoCount(tipoSessao, 
						periodoInicial, periodoFinal, checkAberto, checkFechado, checkPrimeiro, checkSessao, codigoPaciente, tipoFiltroPersonalizado, this.parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_DIAS_PRIMEIRA_CONSULTA)).intValue();
			} catch (ApplicationBusinessException e) {				 
				apresentarExcecaoNegocio(e);
			}			
		}
		return (long) retorno;
	}

	private Boolean validarCamposObrigatorios(Boolean validacao) {
		if(tipoSessao == null){
			apresentarMsgNegocio(Severity.ERROR, "LABEL_TIPO_SESSAO_OBRIGATORIO");			
			dataModel.limparPesquisa();
			dataModel.setPesquisaAtiva(false);
			validacao = true;
		}
		if(DateUtil.validaDataMaior(periodoInicial, periodoFinal)){
			apresentarMsgNegocio(Severity.ERROR, "LABEL_MENSAGEM_COMBINACAO_DATAS");			
			dataModel.limparPesquisa();
			dataModel.setPesquisaAtiva(false);
			validacao = true;
		}
		if(periodoInicial == null){
			apresentarMsgNegocio(Severity.ERROR, "LABEL_PERIODO_INICIAL_OBRIGATORIO");			
			dataModel.limparPesquisa();
			dataModel.setPesquisaAtiva(false);
			validacao = true;
		}
		if(periodoFinal == null){
			apresentarMsgNegocio(Severity.ERROR, "LABEL_PERIODO_FINAL_OBRIGATORIO");			
			dataModel.limparPesquisa();
			dataModel.setPesquisaAtiva(false);
			validacao = true;
		}
		if(!checkAberto && !checkFechado){
			apresentarMsgNegocio(Severity.ERROR, "LABEL_MENSAGEM_TRATAMENTO_OBRIGATORIO");			
			dataModel.limparPesquisa();
			dataModel.setPesquisaAtiva(false);
			validacao = true;
		}
		if(!checkPrimeiro && !checkSessao){
			apresentarMsgNegocio(Severity.ERROR, "LABEL_MENSAGEM_SESSAO_OBRIGATORIO");			
			dataModel.limparPesquisa();
			dataModel.setPesquisaAtiva(false);
			validacao = true;
		}		
		return validacao;
	}

	@Override
	public List<PacientesTratamentoSessaoVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		if(!checkAberto && !checkFechado){
			return null;
		}
		if(!checkPrimeiro && ! checkSessao){
			return null;
		}else{
			if(listaGrid == null){
				try {
					listaGrid = procedimentoTerapeuticoFacade.pesquisarPacientesTratamentoSessao(firstResult, maxResult, orderProperty, asc, tipoSessao, 
							periodoInicial, periodoFinal, checkAberto, checkFechado, checkPrimeiro, checkSessao, codigoPaciente, tipoFiltroPersonalizado, this.parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_DIAS_PRIMEIRA_CONSULTA));
				} catch (ApplicationBusinessException e) {					 
					apresentarExcecaoNegocio(e);
				}		
				listaGridRetorno = listaGrid;
			}else{
				listaGridRetorno = new ArrayList<PacientesTratamentoSessaoVO>();
				maxResult = listaGrid.size();
				for (int i = firstResult; i < maxResult; i++){
					listaGridRetorno.add(listaGrid.get(i));
				}
				
			}
			return listaGridRetorno;	
			
		}
	}

	public Integer getTipoFiltroPersonalizado() {
		return tipoFiltroPersonalizado;
	}

	public void setTipoFiltroPersonalizado(Integer tipoFiltroPersonalizado) {
		this.tipoFiltroPersonalizado = tipoFiltroPersonalizado;
	}

	public MptTipoSessao getTipoSessao() {
		return tipoSessao;
	}

	public void setTipoSessao(MptTipoSessao tipoSessao) {
		this.tipoSessao = tipoSessao;
	}

	public Boolean getCheckAberto() {
		return checkAberto;
	}

	public void setCheckAberto(Boolean checkAberto) {
		this.checkAberto = checkAberto;
	}

	public Boolean getCheckFechado() {
		return checkFechado;
	}

	public void setCheckFechado(Boolean checkFechado) {
		this.checkFechado = checkFechado;
	}

	public Boolean getCheckPrimeiro() {
		return checkPrimeiro;
	}

	public void setCheckPrimeiro(Boolean checkPrimeiro) {
		this.checkPrimeiro = checkPrimeiro;
	}

	public Boolean getCheckSessao() {
		return checkSessao;
	}

	public void setCheckSessao(Boolean checkSessao) {
		this.checkSessao = checkSessao;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public Date getPeriodoInicial() {
		return periodoInicial;
	}

	public void setPeriodoInicial(Date periodoInicial) {
		this.periodoInicial = periodoInicial;
	}

	public Date getPeriodoFinal() {
		return periodoFinal;
	}

	public void setPeriodoFinal(Date periodoFinal) {
		this.periodoFinal = periodoFinal;
	}
	
	public DynamicDataModel<MptPrescricaoPaciente> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<MptPrescricaoPaciente> dataModel) {
		this.dataModel = dataModel;
	}

	public Integer getCodigoPaciente() {
		return codigoPaciente;
	}

	public void setCodigoPaciente(Integer codigoPaciente) {
		this.codigoPaciente = codigoPaciente;
	}

	public RapServidores getServidorLogado() {
		return servidorLogado;
	}

	public void setServidorLogado(RapServidores servidorLogado) {
		this.servidorLogado = servidorLogado;
	}

	public MptFavoritoServidor getFavoritosServidor() {
		return favoritosServidor;
	}

	public void setFavoritosServidor(MptFavoritoServidor favoritosServidor) {
		this.favoritosServidor = favoritosServidor;
	}

	public MptTipoSessao getTipoSessaoUsuario() {
		return tipoSessaoUsuario;
	}

	public void setTipoSessaoUsuario(MptTipoSessao tipoSessaoUsuario) {
		this.tipoSessaoUsuario = tipoSessaoUsuario;
	}

	public List<PacientesTratamentoSessaoVO> getListaGrid() {
		return listaGrid;
	}

	public void setListaGrid(List<PacientesTratamentoSessaoVO> listaGrid) {
		this.listaGrid = listaGrid;
	}
	
	public List<PacientesTratamentoSessaoVO> getListaGridRetorno() {
		return listaGridRetorno;
	}

	public void setListaGridRetorno(
			List<PacientesTratamentoSessaoVO> listaGridRetorno) {
		this.listaGridRetorno = listaGridRetorno;
	}
}
