package br.gov.mec.aghu.prescricaomedica.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.ambulatorio.vo.MamConsultorAmbulatorioVO;
import br.gov.mec.aghu.blococirurgico.vo.EquipeVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghProfEspecialidades;
import br.gov.mec.aghu.model.AghProfEspecialidadesId;
import br.gov.mec.aghu.model.MamConsultorAmbulatorio;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.vo.AghEspecialidadeVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 */

public class VinculoEspecialidadeServidorController extends ActionController {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7913877855730577461L;

	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacadeFacade;
	
	private RapServidores servidorLogado;
	
	private List<AghEspecialidadeVO> listaConsultoriaInternacao;
	private List<MamConsultorAmbulatorioVO> listaConsultoriaAmbulatorio;
	
	private Boolean consultorInternacao;
	private Boolean consultorAmbulatorio;
	private AghEspecialidadeVO especialidadeConsultoriaInternacao;
	private AghEspecialidadeVO especialidadeConsultoriaAmbulatorio;
	private EquipeVO equipeConsultorAmbulatorio;
	
	@PostConstruct
	public void init() {
		this.begin(conversation);
	}
	public void inicio() {
		servidorLogado = servidorLogadoFacade.obterServidorLogado();
		servidorLogado.setPessoaFisica(registroColaboradorFacadeFacade.obterRapPessoasFisicasPorServidor(servidorLogado.getId()));
		pesquisarListas();
		limparConsultoriaInternacao();
		limparConsultoriaAmbulatorio();
	}
	public void pesquisarListas() {
		pesquisarConsultorInternacao();
		pesquisarConsultorAmbulatorio();
	}
	
	private void limparConsultoriaInternacao() {
		this.consultorInternacao = null;
		this.especialidadeConsultoriaInternacao = null;
	}
	
	private void limparConsultoriaAmbulatorio() {
		this.consultorAmbulatorio = null;
		this.especialidadeConsultoriaAmbulatorio = null;
	}
	
	public void pesquisarConsultorInternacao() {
		listaConsultoriaInternacao = aghuFacade.pesquisarAghEspecialidadePorServidor(servidorLogado);
	}
	
	public void pesquisarConsultorAmbulatorio() {
		listaConsultoriaAmbulatorio = ambulatorioFacade.pesquisarConsultorAmbulatorioPorServidor(servidorLogado);
	}
	
	public void desabilitarEquipe() {
		if (especialidadeConsultoriaAmbulatorio == null) {
			equipeConsultorAmbulatorio = null;
		}
	}
	
	public void gravarConsultoriaInternacao() {
		AghProfEspecialidades profEspecialidades = new AghProfEspecialidades();
		
		profEspecialidades.setIndProfRealizaConsultoria(DominioSimNao.getInstance(getConsultorInternacao()));
		try {
			aghuFacade.persistirProfEspecialidades(profEspecialidades, servidorLogado, especialidadeConsultoriaInternacao.getSeq());
			apresentarMsgNegocio(Severity.INFO, "LABEL_INSERIR_CONSULTOR_INTERNACAO", especialidadeConsultoriaInternacao.getEspSigla(), especialidadeConsultoriaInternacao.getEspNomeEspecialidade());
			limparConsultoriaInternacao();
			pesquisarConsultorInternacao();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void ativarDesativarConsultoriaInternacao(AghEspecialidadeVO item) {
		AghProfEspecialidadesId id = new AghProfEspecialidadesId();
		id.setEspSeq(item.getSeq());
		id.setSerMatricula(servidorLogado.getId().getMatricula());
		id.setSerVinCodigo(servidorLogado.getId().getVinCodigo());
		AghProfEspecialidades profEspecialidades = aghuFacade.obterAghProfEspecialidadesPorChavePrimaria(id);
		
		profEspecialidades.setIndProfRealizaConsultoria(item.getPreIndProfRealizaConsultoria());
		profEspecialidades.setServidorDigitador(servidorLogado);
		try {
			aghuFacade.persistirProfEspecialidades(profEspecialidades, servidorLogado, item.getSeq());
			apresentarMsgNegocio(Severity.INFO, "LABEL_ATUALIZAR_CONSULTOR_INTERNACAO", item.getEspSigla(), item.getEspNomeEspecialidade());
			pesquisarConsultorInternacao();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void ativarDesativarConsultoriaAmbulatorio(MamConsultorAmbulatorioVO item) {
		MamConsultorAmbulatorio mamConsultorAmbulatorio = ambulatorioFacade.obterMamConsultorAmbulatorioPorId(item.getMcaSeq());
		mamConsultorAmbulatorio.setIndSituacao(item.getMcaIndSituacao());
		try {
			ambulatorioFacade.persistirConsultorAmbulatorio(mamConsultorAmbulatorio);
			if (item.getEqpNome() != null) {
				apresentarMsgNegocio(Severity.INFO, "LABEL_ATUALIZAR_CONSULTOR_AMBULATORIO", item.getEspSigla(), item.getEspNomeEespecialidade(), item.getEqpNome());
			} else {
				apresentarMsgNegocio(Severity.INFO, "LABEL_ATUALIZAR_CONSULTOR_AMBULATORIO_SEM_EQUIPE", item.getEspSigla(), item.getEspNomeEespecialidade());
			}
			
			pesquisarConsultorAmbulatorio();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void gravarConsultoriaAmbulatorio() {
		MamConsultorAmbulatorio mamConsultorAmbulatorio = new MamConsultorAmbulatorio();
		mamConsultorAmbulatorio.setRapServidores(servidorLogado);
		mamConsultorAmbulatorio.setIndSituacao(DominioSituacao.getInstance(getConsultorAmbulatorio()));
		AghEspecialidades especialidadeAmbulatorio = aghuFacade.obterAghEspecialidadesPorChavePrimaria(especialidadeConsultoriaAmbulatorio.getSeq());
		mamConsultorAmbulatorio.setAghEspecialidadesByEspSeq(especialidadeAmbulatorio);
		if (equipeConsultorAmbulatorio != null && equipeConsultorAmbulatorio.getSeq() != null) {
			AghEquipes equipe = aghuFacade.obterEquipe(equipeConsultorAmbulatorio.getSeq());
			mamConsultorAmbulatorio.setAghEquipes(equipe);
		}
		try {
			ambulatorioFacade.persistirConsultorAmbulatorio(mamConsultorAmbulatorio);
			if (mamConsultorAmbulatorio.getAghEquipes() != null) {
				apresentarMsgNegocio(Severity.INFO, "LABEL_INSERIR_CONSULTOR_AMBULATORIO", especialidadeConsultoriaAmbulatorio.getEspSigla(), especialidadeConsultoriaAmbulatorio.getEspNomeEspecialidade(), equipeConsultorAmbulatorio.getEquipe());
			} else {
				apresentarMsgNegocio(Severity.INFO, "LABEL_INSERIR_CONSULTOR_AMBULATORIO_SEM_EQUIPE", especialidadeConsultoriaAmbulatorio.getEspSigla(), especialidadeConsultoriaAmbulatorio.getEspNomeEspecialidade());
			}
			
			limparConsultoriaAmbulatorio();
			pesquisarConsultorAmbulatorio();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public List<AghEspecialidadeVO> pesquisarEspecialidadesConsultoriaInternacao(final String pesquisa) {
		return this.returnSGWithCount(aghuFacade.pesquisarEspecialidadesConsultoria(pesquisa, false, 0, 100, null, true),aghuFacade.pesquisarEspecialidadesConsultoriaCount(pesquisa, false));
	}
	
	public List<EquipeVO> pesquisarEquipesConsultoriaAmbulatorial(final String pesquisa) {
		return this.returnSGWithCount(aghuFacade.pesquisarEquipesConsultoriaAmbulatorial(pesquisa, especialidadeConsultoriaAmbulatorio.getSeq(), 0, 100, null, true),aghuFacade.pesquisarEquipesConsultoriaAmbulatorialCount(pesquisa, especialidadeConsultoriaAmbulatorio.getSeq()));
	}
	
	public List<AghEspecialidadeVO> pesquisarEspecialidadesConsultoriaAmbulatorio(final String pesquisa) {
		return this.returnSGWithCount(aghuFacade.pesquisarEspecialidadesConsultoria(pesquisa, true, 0, 100, null, true),aghuFacade.pesquisarEspecialidadesConsultoriaCount(pesquisa, true));
	}
	
	public RapServidores getServidorLogado() {
		return servidorLogado;
	}
	public void setServidorLogado(RapServidores servidorLogado) {
		this.servidorLogado = servidorLogado;
	}
	public Boolean getConsultorInternacao() {
		return consultorInternacao;
	}
	public void setConsultorInternacao(Boolean consultorInternacao) {
		this.consultorInternacao = consultorInternacao;
	}
	public Boolean getConsultorAmbulatorio() {
		return consultorAmbulatorio;
	}
	public void setConsultorAmbulatorio(Boolean consultorAmbulatorio) {
		this.consultorAmbulatorio = consultorAmbulatorio;
	}
	public List<AghEspecialidadeVO> getListaConsultoriaInternacao() {
		return listaConsultoriaInternacao;
	}
	public void setListaConsultoriaInternacao(
			List<AghEspecialidadeVO> listaConsultoriaInternacao) {
		this.listaConsultoriaInternacao = listaConsultoriaInternacao;
	}
	public List<MamConsultorAmbulatorioVO> getListaConsultoriaAmbulatorio() {
		return listaConsultoriaAmbulatorio;
	}
	public void setListaConsultoriaAmbulatorio(
			List<MamConsultorAmbulatorioVO> listaConsultoriaAmbulatorio) {
		this.listaConsultoriaAmbulatorio = listaConsultoriaAmbulatorio;
	}
	public AghEspecialidadeVO getEspecialidadeConsultoriaInternacao() {
		return especialidadeConsultoriaInternacao;
	}
	public void setEspecialidadeConsultoriaInternacao(
			AghEspecialidadeVO especialidadeConsultoriaInternacao) {
		this.especialidadeConsultoriaInternacao = especialidadeConsultoriaInternacao;
	}
	public AghEspecialidadeVO getEspecialidadeConsultoriaAmbulatorio() {
		return especialidadeConsultoriaAmbulatorio;
	}
	public void setEspecialidadeConsultoriaAmbulatorio(
			AghEspecialidadeVO especialidadeConsultoriaAmbulatorio) {
		this.especialidadeConsultoriaAmbulatorio = especialidadeConsultoriaAmbulatorio;
	}
	public EquipeVO getEquipeConsultorAmbulatorio() {
		return equipeConsultorAmbulatorio;
	}
	public void setEquipeConsultorAmbulatorio(EquipeVO equipeConsultorAmbulatorio) {
		this.equipeConsultorAmbulatorio = equipeConsultorAmbulatorio;
	}
}
