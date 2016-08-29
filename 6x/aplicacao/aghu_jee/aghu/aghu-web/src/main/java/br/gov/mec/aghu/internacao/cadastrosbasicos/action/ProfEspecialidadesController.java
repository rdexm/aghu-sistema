package br.gov.mec.aghu.internacao.cadastrosbasicos.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;

import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.RapServidoresEspecialidadesVO;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghProfEspecialidades;
import br.gov.mec.aghu.model.AghProfEspecialidadesId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class ProfEspecialidadesController extends ActionController {

	private static final String PAGE_PESQUISAR_PROF_ESPECIALIDADES = "profEspecialidadesList";
	private static final long serialVersionUID = -5895471903970835028L;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	@EJB 
	private IRegistroColaboradorFacade registroColaboradorFacade;

	/**
	 * Especialidade/Profissioanl a ser associado/editada.
	 */
	private AghProfEspecialidades aghProfEspecialidades;
	private AghEspecialidades especialidadeAux = null;
	private RapServidores rapServidores = new RapServidores();
	private RapServidoresEspecialidadesVO servidorVO;

	/**
	 * Lista de especialidades associadas ao profissional.
	 */
	private List<AghProfEspecialidades> listaAghProfEspecialidades;

	private boolean edicao = false;
	private boolean operacaoConcluida = false;
	
	private static final Comparator<AghProfEspecialidades> COMPARATOR_PROFISSIONAL_ESPECIALIDADES= new Comparator<AghProfEspecialidades>() {
		@Override
		public int compare(AghProfEspecialidades o1, AghProfEspecialidades o2) {
			return o1.getAghEspecialidade().getSigla().toUpperCase().compareTo(
					o2.getAghEspecialidade().getSigla().toUpperCase());
		}
	};

	public void init() {
	 
	
		RapServidoresId id = new RapServidoresId(servidorVO.getMatricula(), servidorVO.getVinCodigo());
		this.rapServidores = this.registroColaboradorFacade.obterServidor(id);
		this.listaAghProfEspecialidades = this.cadastrosBasicosInternacaoFacade.listarProfEspecialidadesPorServidor(this.rapServidores);
		Collections.sort(this.listaAghProfEspecialidades, COMPARATOR_PROFISSIONAL_ESPECIALIDADES);
	
	}

	public void novo() {
		AghProfEspecialidadesId aghProfEspecialidadesId = new AghProfEspecialidadesId();
		this.setEspecialidadeAux(null);
		this.aghProfEspecialidades = new AghProfEspecialidades();

		aghProfEspecialidadesId.setSerMatricula(this.rapServidores.getId().getMatricula());
		aghProfEspecialidadesId.setSerVinCodigo(this.rapServidores.getId().getVinCodigo());
		this.aghProfEspecialidades.setId(aghProfEspecialidadesId);
		this.aghProfEspecialidades.setInterna(true);
		this.aghProfEspecialidades.setAmbulatorio(false);
		this.aghProfEspecialidades.setInternacao(true);
		this.aghProfEspecialidades.setCirurgiao(false);
		this.aghProfEspecialidades.setConsultoria(false);
		this.aghProfEspecialidades.setBloco(false);
		this.aghProfEspecialidades.setQuantPacInternados(0);
		this.aghProfEspecialidades.setCapacReferencial(0);
		this.edicao = false;
		this.operacaoConcluida = false;
		associaUsuarioLogado();
	}
	
	public void editar(AghProfEspecialidades aghProfEspecialidades) {
		
		this.setEspecialidadeAux(aghProfEspecialidades.getAghEspecialidade());
		
		this.aghProfEspecialidades = aghProfEspecialidades;
				
		try {
			this.aghProfEspecialidades.setServidorDigitador(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado(), new Date()));
		} catch (ApplicationBusinessException e) {
			this.aghProfEspecialidades.setServidorDigitador(null);
		}

		this.edicao = true;
		this.operacaoConcluida = false;
		associaUsuarioLogado();
	}
	
	public void remover(AghProfEspecialidades aghProfEspecialidades) {
		this.listaAghProfEspecialidades.remove(aghProfEspecialidades);
	}

	public void incluir() {

		try {
			this.aghProfEspecialidades.getId().setEspSeq(this.especialidadeAux.getSeq());
			this.aghProfEspecialidades.setAghEspecialidade(this.especialidadeAux);
			associaUsuarioLogado();
			boolean validado = true;
			
			this.cadastrosBasicosInternacaoFacade.validarDados(this.aghProfEspecialidades);
			if(!this.edicao) {
				for (AghProfEspecialidades aghProfEspAux: this.listaAghProfEspecialidades) {
					if (aghProfEspAux.getId().getEspSeq().equals(this.especialidadeAux.getSeq())) {
						apresentarMsgNegocio(Severity.ERROR, "ERRO_ESPECIALIDADE_PROFISSIONAL_JA_EXISTENTE");
						validado = false;
						this.operacaoConcluida = true;
						break;
					}
				}
			}

			if(validado) {
				getListaAghProfEspecialidades().remove(this.aghProfEspecialidades);
				getListaAghProfEspecialidades().add(this.aghProfEspecialidades);
				Collections.sort(this.listaAghProfEspecialidades, COMPARATOR_PROFISSIONAL_ESPECIALIDADES);
				this.operacaoConcluida = true;
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ADICIONAR_PROFESPECIALIDADES");
			}
		} catch (ApplicationBusinessException e) {
			apresentarMsgNegocio(Severity.ERROR, e.getLocalizedMessage());
		}
	}
	
	/**
	 * Método que realiza a ação do botão gravar na tela de cadastro de
	 */
	public String confirmar() {
		try {
			
			List<AghProfEspecialidades> especialidades = new ArrayList<AghProfEspecialidades>(this.listaAghProfEspecialidades);
			this.cadastrosBasicosInternacaoFacade.persistirProfEspecialidades(especialidades, this.rapServidores.getId());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_PERSISTIR_PROFESPECIALIDADES", this.rapServidores.getPessoaFisica().getNome());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}

		return PAGE_PESQUISAR_PROF_ESPECIALIDADES;
	}
	
	private void associaUsuarioLogado() {
		try {
			RapServidores servidor = registroColaboradorFacade.obterServidorPorUsuario(obterLoginUsuarioLogado());
			this.aghProfEspecialidades.setServidorDigitador(servidor);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String cancelar() {
		return PAGE_PESQUISAR_PROF_ESPECIALIDADES;
	}

	public void validaEspecialidade(){
		if (this.aghProfEspecialidades.getId().getEspSeq() == null){
			apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ESPECIALIDADE_INVALIDA");
		}
	}
	
	public void limparEspecialidade() {
		this.setEspecialidadeAux(null);
	}
	
	public List<AghEspecialidades> listarEspecialidadesAtivasPorSiglaOuDescricao(String param) {
		return this.cadastrosBasicosInternacaoFacade.listarEspecialidadesAtivasPorSiglaOuDescricao(param, true);
	}	
	
	public RapServidores getRapServidores() {
		return this.rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	public AghProfEspecialidades getAghProfEspecialidades() {
		return this.aghProfEspecialidades;
	}

	public void setAghProfEspecialidades(AghProfEspecialidades aghProfEspecialidades) {
		this.aghProfEspecialidades = aghProfEspecialidades;
	}
	
	public List<AghProfEspecialidades> getListaAghProfEspecialidades() {
		return this.listaAghProfEspecialidades;
	}

	public void setListaAghProfEspecialidades(
			List<AghProfEspecialidades> listaAghProfEspecialidades) {
		this.listaAghProfEspecialidades = listaAghProfEspecialidades;
	}

	public boolean isEdicao() {
		return this.edicao;
	}

	public void setEdicao(boolean edicao) {
		this.edicao = edicao;
	}

	public boolean isOperacaoConcluida() {
		return this.operacaoConcluida;
	}

	public void setOperacaoConcluida(boolean operacaoConcluida) {
		this.operacaoConcluida = operacaoConcluida;
	}

	public AghEspecialidades getEspecialidadeAux() {
		return this.especialidadeAux;
	}

	public void setEspecialidadeAux(AghEspecialidades especialidadeAux) {
		this.especialidadeAux = especialidadeAux;
	}

	public RapServidoresEspecialidadesVO getServidorVO() {
		return servidorVO;
	}

	public void setServidorVO(RapServidoresEspecialidadesVO servidorVO) {
		this.servidorVO = servidorVO;
	}
}