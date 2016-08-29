package br.gov.mec.aghu.paciente.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.business.fonetizador.FonetizadorUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.dominio.DominioListaOrigensAtendimentos;
import br.gov.mec.aghu.model.AipFonemaPacientes;
import br.gov.mec.aghu.model.AipFonemas;
import br.gov.mec.aghu.model.AipFonemasMaePaciente;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.paciente.business.exception.PacienteExceptionCode;
import br.gov.mec.aghu.paciente.dao.AipFonemaPacientesDAO;
import br.gov.mec.aghu.paciente.dao.AipFonemasDAO;
import br.gov.mec.aghu.paciente.dao.AipFonemasMaePacienteDAO;
import br.gov.mec.aghu.paciente.dao.AipPacientesDAO;
import br.gov.mec.aghu.paciente.dao.AipPacientesRotinaFonemaDAO;
import br.gov.mec.aghu.paciente.dao.AipPosicaoFonemasPacientesRotinaFonemaDAO;

/**
 * 
 * Classe de apoio para as Business Facades. Ela em geral agrupa as
 * funcionalidades encontradas em packages e procedures do AGHU.
 * 
 * Ela poderia ser uma classe com acesso friendly ou default e não ser um
 * componente seam.
 * 
 * Mas fazendo assim facilita, pois ela também pode receber uma referência para
 * o EntityManager.
 * 
 * Outra forma de fazer é instanciar ela diretamente do ON e passar o entity
 * manager para seus parâmetros. Neste caso os metodos desta classe poderiam ser
 * até estaticos e nao necessitar de instanciação. Ai ela seria apenas um
 * particionamento lógico de código e não um componente que possa ser injetado
 * em qualquer outro contexto.
 * 
 * ATENÇÃO, Os metodos desta classe nunca devem ser acessados diretamente por
 * qualquer classe que não a ON correspondente. Por isso sugere-se que todos os
 * métodos desta sejam friendly (default) ou private.
 * 
 */
@Stateless
public class FonemasPacienteRN extends BaseBusiness {

		
	private static final Log LOG = LogFactory.getLog(FonemasPacienteRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	@Inject
	private AipPacientesDAO aipPacientesDAO;
	
	@Inject
	private AipFonemasDAO aipFonemasDAO;
	
	@Inject
	private AipPosicaoFonemasPacientesRotinaFonemaDAO aipPosicaoFonemasPacientesRotinaFonemaDAO;
	
	@Inject
	private AipPacientesRotinaFonemaDAO aipPacientesRotinaFonemaDAO;
	
	@Inject
	private AipFonemasMaePacienteDAO aipFonemasMaePacienteDAO;
	
	@Inject
	private AipFonemaPacientesDAO aipFonemaPacientesDAO;
	
	private static final long serialVersionUID = -6705607345354507862L;

	/**
	 * Implementação da função AIPC_WHERE_FONEMA e AIPC_WHERE_FNEMA_MAE. Este
	 * método é responsável por retornar os códigos dos pacientes que condizem
	 * que o nome do paciente e da mãe informado.
	 * 
	 * @return lista de códigos dos paciente.
	 */
	
	@Deprecated
	public List<AipPacientes> obterPacientes(String nomePaciente, String nomeMae,
			Calendar dataInicio, Calendar dataLimite, boolean respeitarOrdem,
			Integer firstResult, Integer maxResults, DominioListaOrigensAtendimentos listaOrigensAtendimentos) throws ApplicationBusinessException {
		List<String> fonemasPaciente = obterFonemasNaOrdem(nomePaciente);
		List<String> fonemasMae = obterFonemasNaOrdem(nomeMae);

		if ((fonemasPaciente == null || fonemasPaciente.isEmpty())
				&& (fonemasMae == null || fonemasMae.isEmpty())) {
			return new ArrayList<AipPacientes>(0);
		} else {
			return obterPacientesPorFonemas(fonemasPaciente, fonemasMae,
					dataInicio, dataLimite, respeitarOrdem, firstResult,
					maxResults, listaOrigensAtendimentos);
		}
	}
	
	public List<AipPacientes> obterPacientes(PesquisaFoneticaPaciente parametrosPequisa) throws ApplicationBusinessException {
		verificaPeloMenosUmCampoFoneticoPreenchido(parametrosPequisa);
		return this.obterPacientesPorFonemas(parametrosPequisa);
		
	}
	
	public Long obterPacientesCount(PesquisaFoneticaPaciente parametrosPequisa) throws ApplicationBusinessException {
		verificaPeloMenosUmCampoFoneticoPreenchido(parametrosPequisa);
		return converteRetornoCount(this.obterPacientesPorFonemas(parametrosPequisa));
	}
	
	private Long converteRetornoCount(List<AipPacientes> listaPacientes) {
		return listaPacientes.isEmpty() ? Long.valueOf("0") : Long.valueOf(((Object)listaPacientes.get(0)).toString());
	}
	
	private void verificaPeloMenosUmCampoFoneticoPreenchido(PesquisaFoneticaPaciente parametrosPequisa) throws ApplicationBusinessException {
		if (StringUtils.isBlank(parametrosPequisa.getNome()) && StringUtils.isBlank(parametrosPequisa.getNomeMae()) 
				&& StringUtils.isBlank(parametrosPequisa.getNomeSocial())) {
			throw new ApplicationBusinessException(PacienteExceptionCode.MENSAGEM_ERRO_PESQUISA_FONETICA_NOME_OBRIGATORIO);
		}
		
	}
	
	private List<AipPacientes> obterPacientesPorFonemas(PesquisaFoneticaPaciente parametrosPequisa) throws ApplicationBusinessException {
		List<String> fonemasPaciente = obterFonemasNaOrdem(parametrosPequisa.getNome());
		List<String> fonemasMae = obterFonemasNaOrdem(parametrosPequisa.getNomeMae());
		List<String> fonemasNomeSocial = obterFonemasNaOrdem(parametrosPequisa.getNomeSocial());
		
		if (fonemasPaciente.isEmpty() && fonemasMae.isEmpty() && fonemasNomeSocial.isEmpty()) {
			return new ArrayList<AipPacientes>();
		}
		return this.getAipPacientesDAO().obterPacientesPorFonemas(parametrosPequisa, fonemasPaciente, fonemasMae, fonemasNomeSocial);
	}

	public Long obterPacientesCount(String nomePaciente, String nomeMae,
			Calendar dataInicio, Calendar dataLimite, boolean respeitarOrdem, DominioListaOrigensAtendimentos listaOrigensAtendimentos) throws ApplicationBusinessException {
		List<String> fonemasPaciente = obterFonemasNaOrdem(nomePaciente);
		List<String> fonemasMae = obterFonemasNaOrdem(nomeMae);

		if ((fonemasPaciente == null || fonemasPaciente.isEmpty())
				&& (fonemasMae == null || fonemasMae.isEmpty())) {
			return 0L;
		} else {
			return obterPacientesPorFonemasCount(fonemasPaciente, fonemasMae,
					dataInicio, dataLimite, respeitarOrdem, listaOrigensAtendimentos);
		}
	}
		
	@Deprecated	
	private List<AipPacientes> obterPacientesPorFonemas(List<String> fonemasPaciente, List<String> fonemasMae, Calendar dataInicio,
			Calendar dataLimite, boolean respeitarOrdem, Integer firstResult, Integer maxResults, DominioListaOrigensAtendimentos listaOrigensAtendimentos) {
		return this.getAipPacientesDAO().obterPacientesPorFonemas(fonemasPaciente, fonemasMae, dataInicio, dataLimite, respeitarOrdem,
				firstResult, maxResults, listaOrigensAtendimentos, false);
	}

	private Long obterPacientesPorFonemasCount(List<String> fonemasPaciente, List<String> fonemasMae, Calendar dataInicio,
			Calendar dataLimite, boolean respeitarOrdem, DominioListaOrigensAtendimentos listaOrigensAtendimentos) {
		return this.getAipPacientesDAO().obterPacientesPorFonemasCount(fonemasPaciente, fonemasMae, dataInicio, dataLimite,
				respeitarOrdem, listaOrigensAtendimentos, false);
	}

	/**
	 * Retorna os fonemas associados ao nome e na ordem em que foram digitados.
	 * 
	 * @param nome nome a ser fonetizado.
	 * @return fonemas relacionados ao nome informado.
	 */
	public List<String> obterFonemasNaOrdem(String nome) throws ApplicationBusinessException {
		if (StringUtils.isBlank(nome)) {
			return new ArrayList<String>(0);
		}

		String pFonema = FonetizadorUtil.obterFonema(nome);
		List<String> fonemas = particionarFonemas(pFonema);

		return fonemas;
	}

	/**
	 * Cada fonema tem um tamanho de no máximo 6 caracteres, portanto o tamanho
	 * da string passada como parâmetro sempre vai ser múltiplo de 6.
	 */
	private List<String> particionarFonemas(String pFonema) {
		if (StringUtils.isBlank(pFonema)) {
			return new ArrayList<String>(0);
		}

		List<String> fonemas = new ArrayList<String>();

		int o = (pFonema.length() / 6);
		for (int i = 1; i <= o; i++) {
			fonemas.add(pFonema.substring((6 * (i - 1)), (6 * (i - 1)) + 6));
		}

		return fonemas;
	}

	/**
	 * Método que obtém um objeto AipFonema pelo seu fonema
	 */
	public AipFonemas obterAipFonemaPorFonema(String fonema){
		return this.getAipFonemasDAO().obterAipFonemaPorFonema(fonema);
	}
	
	/**
	 * Método que remove um objeto AipFonema 
	 */
	public void removerAipFonema(AipFonemas aipFonema){
		try {
			this.getAipFonemasDAO().remover(aipFonema);
		} catch (Exception e) {
			LOG.error("Erro ao remover o fonema " + aipFonema.getFonema(), e);
		}
	}
	
	/**
	 * Método que remove um objeto AipFonemaPaciente 
	 */
	public void removerAipFonemaPaciente(AipFonemaPacientes aipFonemaPaciente){
		try {
			this.getAipFonemaPacientesDAO().remover(aipFonemaPaciente);
		} catch (Exception e) {
			LOG.error("Erro ao remover o fonema " + aipFonemaPaciente.getAipFonemas().getFonema()  + " do paciente " + aipFonemaPaciente.getAipPaciente().getNome(), e);
		}
	}
	
	/**
	 * Método que remove um objeto AipFonemaMaePaciente 
	 */
	public void removerAipFonemaMaePaciente(AipFonemasMaePaciente aipFonemaMaePaciente) {
		try {
			this.getAipFonemasMaePacienteDAO().remover(aipFonemaMaePaciente);
		} catch (Exception e) {
			LOG.error("Erro ao remover fonema " + aipFonemaMaePaciente.getAipFonemas().getFonema() + " da mãe do paciente" + aipFonemaMaePaciente.getAipPaciente().getNome(), e);
		}
	}
	
	/**
	 * Método que persiste um objeto AipFonema 
	 */
	public void persistirAipFonema(AipFonemas aipFonema){
		this.getAipFonemasDAO().persistir(aipFonema);
	}
	
	/**
	 * Método que obtém um objeto AipFonemaPaciente pelo paciente e o fonema
	 */
	public AipFonemaPacientes obterAipFonemaPaciente(Integer codigoPaciente, AipFonemas aipFonema){
		return this.getAipFonemaPacientesDAO().obterAipFonemaPaciente(codigoPaciente, aipFonema);
	}
	
	/**
	 * Método que obtém um objeto AipFonemaMaePaciente pelo paciente e o fonema
	 */
	public AipFonemasMaePaciente obterAipFonemaMaePaciente(Integer codigoPaciente, AipFonemas aipFonema){
		return this.getAipFonemasMaePacienteDAO().obterAipFonemaMaePaciente(codigoPaciente, aipFonema);
	}
	
	/**
	 * Método que obtém todos objetos AipFonemaPaciente pelo código do paciente
	 */
	public List<AipFonemaPacientes> pesquisarFonemasPaciente(Integer codigoPaciente){
		return this.getAipFonemaPacientesDAO().pesquisarFonemasPaciente(codigoPaciente);
	}
	
	/**
	 * Método que obtém todos objetos AipFonemaMaePaciente pelo código do paciente
	 */
	public List<AipFonemasMaePaciente> pesquisarFonemasMaePaciente(Integer codigoPaciente){
		return this.getAipFonemasMaePacienteDAO().pesquisarFonemasMaePaciente(codigoPaciente);
	}
		
	protected AipFonemaPacientesDAO getAipFonemaPacientesDAO() {
		return aipFonemaPacientesDAO;
	}
	
	protected AipFonemasDAO getAipFonemasDAO() {
		return aipFonemasDAO;
	}

	protected AipFonemasMaePacienteDAO getAipFonemasMaePacienteDAO() {
		return aipFonemasMaePacienteDAO;
	}
	
	protected AipPacientesDAO getAipPacientesDAO() {
		return aipPacientesDAO;
	}
	
	protected AipPacientesRotinaFonemaDAO getAipPacientesRotinaFonemaDAO() {
		return aipPacientesRotinaFonemaDAO;
	}
	
	protected AipPosicaoFonemasPacientesRotinaFonemaDAO getAipPosicaoFonemasPacientesRotinaFonemaDAO() {
		return aipPosicaoFonemasPacientesRotinaFonemaDAO;
	}
}