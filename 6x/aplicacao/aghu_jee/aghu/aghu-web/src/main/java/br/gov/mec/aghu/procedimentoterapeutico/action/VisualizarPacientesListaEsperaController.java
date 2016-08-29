package br.gov.mec.aghu.procedimentoterapeutico.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MptFavoritoServidor;
import br.gov.mec.aghu.model.MptTipoSessao;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ListaEsperaRetirarPacienteVO;
import br.gov.mec.aghu.protocolos.vo.CadIntervaloTempoVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

public class VisualizarPacientesListaEsperaController extends ActionController {

	private static final long serialVersionUID = 5402955523738594923L;

	@EJB
	private IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IAghuFacade AghuFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
    private IParametroFacade parametroFacade;

	@Inject
	private AgendamentoSessaoController agendamentoSessaoController;
	
	private MptFavoritoServidor favoritoServidor;
	private MptTipoSessao tipoSessao;
	private List<MptTipoSessao> listaTipoSessao;
	private Date dataPrescricao;
	private Date dataSugerida;
	private AghEspecialidades especialidade;
	private AipPacientes paciente;
	private static final String PESQUISA_FONETICA = "paciente-pesquisaPacienteComponente";
	private static final String PAGE_AGENDAMENTO_SESSAO = "procedimentoterapeutico-agendamentoSessao";
	private List<ListaEsperaRetirarPacienteVO> listaItens;
	private static final String P_AGHU_VALIDADE_PRECRICAO_QUIMIO = "P_AGHU_VALIDADE_PRECRICAO_QUIMIO";	
	private List<CadIntervaloTempoVO> consultaC5;
	private Boolean pesquisou = false;
	
	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}
	
	public void iniciar(){
		try {
			favoritoServidor = this.procedimentoTerapeuticoFacade.obterFavoritoPorServidor(this.registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()));
		} catch (ApplicationBusinessException e) {			 
			apresentarExcecaoNegocio(e);
		}		
		
		if(favoritoServidor != null){
			this.tipoSessao = this.procedimentoTerapeuticoFacade.obterTipoSessaoOriginal(favoritoServidor.getSeqTipoSessao());
		}
		listaTipoSessao = this.procedimentoTerapeuticoFacade.buscarTipoSessao();		
	}
	
	public List<AghEspecialidades> pesquisarPorNomeOuSiglaOuNomeRedOuNomeEsp(String parametro) {
		return this.AghuFacade.pesquisarEspAtivaPorSeqOuOuSiglaOuNomeRedOuNomeEsp(parametro);
	}
	
	public void pesquisaPaciente(ValueChangeEvent event){
		try {
			paciente = pacienteFacade.pesquisarPacienteComponente(event.getNewValue(), event.getComponent().getId());
		}catch(BaseException e){
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String redirecionarPesquisaFonetica() {
		return PESQUISA_FONETICA;
	}
	
	public void pesquisar(){
		List<ListaEsperaRetirarPacienteVO> remocao = new ArrayList<ListaEsperaRetirarPacienteVO>();
		listaItens = this.procedimentoTerapeuticoFacade.pesquisarListaEsperaPacientes(this.parametroFacade.obterAghParametroPorNome(P_AGHU_VALIDADE_PRECRICAO_QUIMIO), this.tipoSessao, this.dataPrescricao, this.dataSugerida, this.especialidade, this.paciente);
		for(ListaEsperaRetirarPacienteVO item:listaItens){
			Long resultado = this.procedimentoTerapeuticoFacade.removerItensRN07(item.getLote(), item.getCloSeq());
			if(resultado == 0){
				remocao.add(item);
			}
		}
		if(remocao.size() > 0){
			for(ListaEsperaRetirarPacienteVO r:remocao){
				listaItens.remove(r);
			}			
		}
		pesquisou = true;
	}
	
	public void limpar(){
		this.tipoSessao = null;
		this.dataPrescricao = null;
		this.dataSugerida = null;
		this.especialidade = null;
		this.paciente = null;
		this.listaItens = null;
		pesquisou = false;
	}
	
	public String buscarProtocolos(Integer cloSeq){
		return this.procedimentoTerapeuticoFacade.buscarProtocolos(cloSeq);
	}
	
	public String buscarNomeResponsavel(String nome1, String nome2){
		return this.procedimentoTerapeuticoFacade.buscarNomeResponsavel(nome1, nome2);
	}
	
	public String redirecionarTelaAgendamento(Integer lote, Integer pacCodigo, Short ciclo){
		consultaC5 = this.procedimentoTerapeuticoFacade.consultarDiasAgendamento(lote);
		agendamentoSessaoController.setTipoSessao(tipoSessao);
		agendamentoSessaoController.setPaciente(this.procedimentoTerapeuticoFacade.obterPacientePorPacCodigo(pacCodigo));
		agendamentoSessaoController.setTelaLista(true);
		try {
			agendamentoSessaoController.obterListaPrescricoesPorPaciente();
		} catch (ApplicationBusinessException e) {			 
			apresentarExcecaoNegocio(e);
		}						
		return PAGE_AGENDAMENTO_SESSAO;
	}
	
	public String obterHintProtocolo(String descricao) {
		if (StringUtils.isNotBlank(descricao) && descricao.length() > 15) {
			descricao = StringUtils.abbreviate(descricao, 15);
		}
		return descricao;
	}
	
	public String obterHintEspecialidade(String descricao) {
		if (StringUtils.isNotBlank(descricao) && descricao.length() > 22) {
			descricao = StringUtils.abbreviate(descricao, 22);
		}
		return descricao;
	}
	
	public String obterHintNomeResponsavel(String descricao) {
		if (StringUtils.isNotBlank(descricao) && descricao.length() > 29) {
			descricao = StringUtils.abbreviate(descricao, 29);
		}
		return descricao;
	}
	
	public void obterPacientePorPacCodigo(Integer pacCodigo){
		paciente = this.procedimentoTerapeuticoFacade.obterPacientePorPacCodigo(pacCodigo);
	}

	public MptFavoritoServidor getFavoritoServidor() {
		return favoritoServidor;
	}


	public void setFavoritoServidor(MptFavoritoServidor favoritoServidor) {
		this.favoritoServidor = favoritoServidor;
	}

	public MptTipoSessao getTipoSessao() {
		return tipoSessao;
	}

	public void setTipoSessao(MptTipoSessao tipoSessao) {
		this.tipoSessao = tipoSessao;
	}

	public List<MptTipoSessao> getListaTipoSessao() {
		return listaTipoSessao;
	}

	public void setListaTipoSessao(List<MptTipoSessao> listaTipoSessao) {
		this.listaTipoSessao = listaTipoSessao;
	}

	public Date getDataPrescricao() {
		return dataPrescricao;
	}

	public void setDataPrescricao(Date dataPrescricao) {
		this.dataPrescricao = dataPrescricao;
	}

	public Date getDataSugerida() {
		return dataSugerida;
	}

	public void setDataSugerida(Date dataSugerida) {
		this.dataSugerida = dataSugerida;
	}

	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public List<ListaEsperaRetirarPacienteVO> getListaItens() {
		return listaItens;
	}

	public void setListaItens(List<ListaEsperaRetirarPacienteVO> listaItens) {
		this.listaItens = listaItens;
	}

	public List<CadIntervaloTempoVO> getConsultaC5() {
		return consultaC5;
	}

	public void setConsultaC5(List<CadIntervaloTempoVO> consultaC5) {
		this.consultaC5 = consultaC5;
	}

	public Boolean getPesquisou() {
		return pesquisou;
	}

	public void setPesquisou(Boolean pesquisou) {
		this.pesquisou = pesquisou;
	}		
}
