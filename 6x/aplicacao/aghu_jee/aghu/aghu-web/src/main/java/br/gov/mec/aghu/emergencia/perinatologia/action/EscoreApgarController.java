package br.gov.mec.aghu.emergencia.perinatologia.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;

import br.gov.mec.aghu.emergencia.business.IEmergenciaFacade;
import br.gov.mec.aghu.model.McoApgars;
import br.gov.mec.aghu.model.McoApgarsId;
import br.gov.mec.aghu.model.McoRecemNascidos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.vo.Paciente;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.vo.Servidor;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;

public class EscoreApgarController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 638726387236826L;

	private static final String REDIRECIONA_PESQUISAR_GESTACOES = "registrarGestacao"; 
	
	@EJB 
	private IEmergenciaFacade emergenciaFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@Inject
	private RegistrarGestacaoAbaRecemNascidoController registrarGestacaoAbaRecemNascidoController;
	
	private Integer pacCodigo;
	
	private String dadosRecemNascido; 
	private Servidor elaborador;
	
	private McoRecemNascidos recemNascido;
	private McoApgars apgar;
	
	@PostConstruct
	public void init() {
		begin(conversation, true);
		apgar = new McoApgars();
	}

	public void carregarDados() {
		this.obterDadosRecemNascido();
	}
	
	private void obterDadosRecemNascido() {
		recemNascido  = emergenciaFacade.obterRecemNascidosPorCodigo(pacCodigo);
		try {
			Paciente paciente = emergenciaFacade.obterPacienteRecemNascido(pacCodigo);
			dadosRecemNascido = paciente.getNome() + " - " + DateUtil.obterDataFormatada(recemNascido.getDthrNascimento(), "dd/MM/yyyy") + " - Prontuário: " + CoreUtil.formataProntuario(paciente.getProntuario()) + " - Código: "  + pacCodigo;
		
			List<McoApgars> apgars = emergenciaFacade.obterListaApgarPorCodigoPaciente(pacCodigo, null, null);
			if(apgars != null && !apgars.isEmpty()) {
				apgar = (McoApgars)CollectionUtils.get(apgars, 0);
				elaborador = registroColaboradorFacade.obterVRapPessoaServidorPorVinCodMatricula(apgar.getId().getSerMatricula(), apgar.getId().getSerVinCodigo());
			}

			RapServidores servidor = registroColaboradorFacade.obterServidorAtivoPorUsuario(super.obterLoginUsuarioLogado());
			apgars = emergenciaFacade.obterListaApgarPorCodigoPaciente(pacCodigo, servidor.getId().getMatricula(), servidor.getId().getVinCodigo());
			if(apgars != null && !apgars.isEmpty()) {
				apgar = (McoApgars)CollectionUtils.get(apgars, 0);
			}
			else {
				apgar =  new McoApgars();
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public String gravar() {
		boolean edicao = true;
		if(apgar.getId() == null) {
			McoApgarsId id = new McoApgarsId(recemNascido.getId().getGsoPacCodigo(), recemNascido.getId().getGsoSeqp(), recemNascido.getId().getSeqp(), null, null);
			apgar.setId(id);
			apgar.setPacCodigo(pacCodigo);
			edicao = false;
		}
		try {
			emergenciaFacade.persistirApgar(apgar);
			if(!edicao) {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_APGAR_INSERIDO_SUCESSO");
			} else {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_APGAR_ALTERADO_SUCESSO");
			}
			
			registrarGestacaoAbaRecemNascidoController.inicio();
			
			return REDIRECIONA_PESQUISAR_GESTACOES;
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		
		return null;
	}
	
	public String voltar() {
		return REDIRECIONA_PESQUISAR_GESTACOES;
	}
	
	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public String getDadosRecemNascido() {
		return dadosRecemNascido;
	}

	public void setDadosRecemNascido(String dadosRecemNascido) {
		this.dadosRecemNascido = dadosRecemNascido;
	}

	public Servidor getElaborador() {
		return elaborador;
	}

	public void setElaborador(Servidor elaborador) {
		this.elaborador = elaborador;
	}

	public McoApgars getApgar() {
		return apgar;
	}

	public void setApgar(McoApgars apgar) {
		this.apgar = apgar;
	}
}
