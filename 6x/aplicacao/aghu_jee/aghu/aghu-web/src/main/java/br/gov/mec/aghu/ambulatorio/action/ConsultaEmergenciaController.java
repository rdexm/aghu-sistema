package br.gov.mec.aghu.ambulatorio.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AacGradeAgendamenConsultas;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.core.action.ActionController;

/**
 * Classe responsável por controlar as ações de 'Marcar consulta não programada
 * de emergência'.
 * 
 * @author Ricardo Costa
 */
// TODO: Continuar a implementação dessa estória de usuário #1796. Foi
// abortada pois não faz parte do escopo de Internação. Verificar o escopo
// correto.

public class ConsultaEmergenciaController extends ActionController {

	private static final long serialVersionUID = 973368258538172055L;

	@EJB
	private IAghuFacade aghuFacade;

	private AacGradeAgendamenConsultas gradeAgendamentoConsulta;

	private String sigEsp;

	private String sigDescEspLov;

	private AghEspecialidades aghEsp;

	private List<AghEspecialidades> aghEsps = new ArrayList<AghEspecialidades>(0);

	private Integer seqEquipe;

	private String seqDescEquipeLov;

	private AghEquipes aghEquipe;

	private List<AghEquipes> aghEquipes = new ArrayList<AghEquipes>(0);

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public List<AghEspecialidades> pesquisarEspecialidades(String param) {
		return cadastrosBasicosInternacaoFacade.pesquisarEspecialidadePorSiglaNome(param);
	}

	public List<AghEquipes> pesquisarEquipes(String param) {
		return aghuFacade.pesquisarEquipesPorNomeOuDescricao(param);
	}

	public String confirmar() {
		// TODO: Continuar a implementação dessa estória de usuário #1796. 
		// Foi abortada pois não faz parte do escopo de Internação. Verificar o escopo correto.

		return null;
	}

	public String cancelar() {
		return null;
	}

	public AacGradeAgendamenConsultas getGradeAgendamentoConsulta() {
		return gradeAgendamentoConsulta;
	}

	public void setGradeAgendamentoConsulta(
			AacGradeAgendamenConsultas gradeAgendamentoConsulta) {
		this.gradeAgendamentoConsulta = gradeAgendamentoConsulta;
	}

	public String getSigDescEspLov() {
		return sigDescEspLov;
	}

	public void setSigDescEspLov(String sigDescEspLov) {
		this.sigDescEspLov = sigDescEspLov;
	}

	public AghEspecialidades getAghEsp() {
		return aghEsp;
	}

	public void setAghEsp(AghEspecialidades aghEsp) {
		this.aghEsp = aghEsp;
	}

	public List<AghEspecialidades> getAghEsps() {
		return aghEsps;
	}

	public void setAghEsps(List<AghEspecialidades> aghEsps) {
		this.aghEsps = aghEsps;
	}

	public String getSigEsp() {
		return sigEsp;
	}

	public void setSigEsp(String sigEsp) {
		this.sigEsp = sigEsp;
	}

	public Integer getSeqEquipe() {
		return seqEquipe;
	}

	public void setSeqEquipe(Integer seqEquipe) {
		this.seqEquipe = seqEquipe;
	}

	public String getSeqDescEquipeLov() {
		return seqDescEquipeLov;
	}

	public void setSeqDescEquipeLov(String seqDescEquipeLov) {
		this.seqDescEquipeLov = seqDescEquipeLov;
	}

	public AghEquipes getAghEquipe() {
		return aghEquipe;
	}

	public void setAghEquipe(AghEquipes aghEquipe) {
		this.aghEquipe = aghEquipe;
	}

	public List<AghEquipes> getAghEquipes() {
		return aghEquipes;
	}

	public void setAghEquipes(List<AghEquipes> aghEquipes) {
		this.aghEquipes = aghEquipes;
	}

}
