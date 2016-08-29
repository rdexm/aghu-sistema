package br.gov.mec.aghu.internacao.pesquisa.action;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.internacao.pesquisa.business.IPesquisaInternacaoFacade;
import br.gov.mec.aghu.internacao.solicitacao.business.ISolicitacaoInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.ProfessorVO;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.core.action.ActionController;



public class PesquisarPacienteInternadoController extends ActionController {

	private static final String _HIFEN_ = " - ";

	/**
	 * 
	 */
	private static final long serialVersionUID = -3205958071223382023L;

	@EJB
	private ISolicitacaoInternacaoFacade solicitacaoInternacaoFacade;

	@EJB
	private IPesquisaInternacaoFacade pesquisaInternacaoFacade;
	
	private Integer internacaoSeq;
	
	//atributos para exibir detalhes
	private String leito;
	private Integer prontuario;
	private String nomePaciente;
	private Date dthrInternacao;
	private Date dthrAltaMedica;
	private String descricaoCRM;
	private String descricaoEspecialidade;
	private String descricaoConvenio;
	private String descricaoQuarto;
	private String descricaoUnidadeFuncional;
	//exibir botoes de dar alta e transferencia
	private boolean indPacienteInternado;
	
	private final String PAGE_PESQUISAR_PACIENTE_INTERNADO = "pesquisarPacienteInternado";
	private final String PAGE_PESQUISAR_DISPONIBILIDADE_LEITO = "pesquisarDisponibilidadeLeito";
	private final String PAGE_DAR_ALTA = "internacao-dadosDaAltaPaciente";
	private final String PAGE_SOLICITAR_TRANSFERENCIA = "internacao-solicitaTransferenciaPacienteList";

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}
	
	@SuppressWarnings("PMD.NPathComplexity")
	public void inicio(){
		this.leito = null;
		this.prontuario = null;
		this.nomePaciente = null;
		this.dthrInternacao = null;
		this.dthrAltaMedica = null;
		this.descricaoCRM = null;
		this.descricaoEspecialidade = null;
		this.descricaoConvenio = null;
		this.descricaoQuarto = null;
		this.descricaoUnidadeFuncional = null;		
		
		AinInternacao internacao = this.pesquisaInternacaoFacade.obterInternacao(this.internacaoSeq);
		if(internacao.getLeito() != null){
			this.leito = internacao.getLeito().getLeitoID();
		}
		if(internacao.getPaciente().getProntuario() != null){
			this.prontuario = internacao.getPaciente().getProntuario();
		}
		this.nomePaciente = internacao.getPaciente().getNome();
		this.dthrInternacao = internacao.getDthrInternacao();
		this.dthrAltaMedica = internacao.getDthrAltaMedica();
		this.indPacienteInternado = internacao.getIndPacienteInternado();
		if(internacao.getServidorProfessor() != null){
			ProfessorVO professorVO = this.solicitacaoInternacaoFacade.pesquisarProfessorVO(internacao.getServidorProfessor().getId());
			if (professorVO != null) {
				this.descricaoCRM = professorVO.getNroRegConselho() + _HIFEN_ + professorVO.getNomeUsual();
			}
		}
		
		if(internacao.getEspecialidade()!= null){
			this.descricaoEspecialidade = internacao.getEspecialidade().getSigla() + _HIFEN_+ internacao.getEspecialidade().getNomeEspecialidade();
		}
		if(internacao.getConvenioSaude() != null){
			StringBuilder sbConvenio = new StringBuilder();
			sbConvenio.append(internacao.getConvenioSaude().getCodigo())
			.append(_HIFEN_)
			.append(internacao.getConvenioSaude().getDescricao())
			.append(_HIFEN_)
			.append(internacao.getConvenioSaudePlano().getDescricao());
			
			this.descricaoConvenio = sbConvenio.toString();
		}
		if(internacao.getQuarto() != null){
			this.descricaoQuarto = internacao.getQuarto().getDescricao(); 
		} else if (internacao.getLeito() != null){
			this.descricaoQuarto = internacao.getLeito().getQuarto().getDescricao() ; 
		}
		AghUnidadesFuncionais unfTemp = null;
		if(internacao.getUnidadesFuncionais() != null){
			unfTemp = internacao.getUnidadesFuncionais();
		}else if(internacao.getQuarto() != null && internacao.getQuarto().getUnidadeFuncional() != null ){
			unfTemp = internacao.getQuarto().getUnidadeFuncional();
		}else if(internacao.getLeito() != null && internacao.getLeito().getQuarto()!= null & internacao.getLeito().getQuarto().getUnidadeFuncional() != null){
			unfTemp = internacao.getLeito().getQuarto().getUnidadeFuncional();
		}
		
		if(unfTemp != null){ 
			StringBuilder sb = new StringBuilder("");
			if(unfTemp.getAndar() != null){
				sb.append(unfTemp.getAndar());
				sb.append(' ');
			}
			if(unfTemp.getIndAla() != null){
				sb.append(unfTemp.getIndAla());
				sb.append(_HIFEN_);
			}
			sb.append(unfTemp.getDescricao());
			
			this.descricaoUnidadeFuncional = sb.toString(); 
		}
	}

	public String solicitarTransferencia(){
		return PAGE_SOLICITAR_TRANSFERENCIA;
	}
	
	public String redirecionarDadosAltaPaciente(){
		return PAGE_DAR_ALTA;
	}
	
	
	public String redirecionarPesquisarDisponibilidadeLeito(){
		return PAGE_PESQUISAR_DISPONIBILIDADE_LEITO;
	}
	

	public String cancelar(){
		return PAGE_PESQUISAR_PACIENTE_INTERNADO;
	}

	//### GETs e SETs ###
	public Integer getInternacaoSeq() {
		return this.internacaoSeq;
	}

	public void setInternacaoSeq(Integer internacaoSeq) {
		this.internacaoSeq = internacaoSeq;
	}

	public String getLeito() {
		return this.leito;
	}

	public void setLeito(String leito) {
		this.leito = leito;
	}

	public Integer getProntuario() {
		return this.prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getNomePaciente() {
		return this.nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public Date getDthrInternacao() {
		return this.dthrInternacao;
	}

	public void setDthrInternacao(Date dthrInternacao) {
		this.dthrInternacao = dthrInternacao;
	}

	public Date getDthrAltaMedica() {
		return this.dthrAltaMedica;
	}

	public void setDthrAltaMedica(Date dthrAltaMedica) {
		this.dthrAltaMedica = dthrAltaMedica;
	}

	public String getDescricaoCRM() {
		return this.descricaoCRM;
	}

	public void setDescricaoCRM(String descricaoCRM) {
		this.descricaoCRM = descricaoCRM;
	}

	public String getDescricaoEspecialidade() {
		return this.descricaoEspecialidade;
	}

	public void setDescricaoEspecialidade(String descricaoEspecialidade) {
		this.descricaoEspecialidade = descricaoEspecialidade;
	}

	public String getDescricaoConvenio() {
		return this.descricaoConvenio;
	}

	public void setDescricaoConvenio(String descricaoConvenio) {
		this.descricaoConvenio = descricaoConvenio;
	}

	public String getDescricaoQuarto() {
		return this.descricaoQuarto;
	}

	public void setDescricaoQuarto(String descricaoQuarto) {
		this.descricaoQuarto = descricaoQuarto;
	}

	public String getDescricaoUnidadeFuncional() {
		return this.descricaoUnidadeFuncional;
	}

	public void setDescricaoUnidadeFuncional(String descricaoUnidadeFuncional) {
		this.descricaoUnidadeFuncional = descricaoUnidadeFuncional;
	}

	public boolean isIndPacienteInternado() {
		return this.indPacienteInternado;
	}

	public void setIndPacienteInternado(boolean indPacienteInternado) {
		this.indPacienteInternado = indPacienteInternado;
	}
}