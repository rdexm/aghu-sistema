package br.gov.mec.aghu.paciente.historico.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.AipEndPacientesHist;
import br.gov.mec.aghu.model.AipEndPacientesHistId;
import br.gov.mec.aghu.model.AipEnderecosPacientes;
import br.gov.mec.aghu.model.AipEnderecosPacientesId;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

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
public class HistoricoEnderecoPacienteRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(HistoricoEnderecoPacienteRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8484808744124536330L;

	private enum HistoricoEnderecoPacienteRNExceptionCode implements BusinessExceptionCode {
		ERRO_EXCLUIR_ENDERECO_PACIENTE_HISTORICO
	}
	
	/**
	 * Método que chama as implementações das triggers responsáveis pela
	 * inserção de um registro da tabela AIP_END_PACIENTES_HIST
	 * 
	 * 
	 * @param pacienteHist
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public void inserirEnderecoPacienteHist(AipEndPacientesHist endPacienteHist) {
		this.getPacienteFacade().inserirEnderecoPacienteHist(endPacienteHist, false);
	}

	/**
	 * Método que chama as implementações das triggers responsáveis pela remoção
	 * de um registro da tabela AIP_END_PACIENTES_HIST
	 * 
	 * 
	 * @param pacienteHist
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public void removerEnderecoPacienteHist(AipEndPacientesHist endPacienteHist) throws ApplicationBusinessException {
		try {
			this.getPacienteFacade().removerEnderecoPacienteHist(endPacienteHist, true);
		} catch (Exception e) {
			LOG.error("Erro ao remover o endereço do paciente no histórico.", e);
			throw new ApplicationBusinessException(HistoricoEnderecoPacienteRNExceptionCode.ERRO_EXCLUIR_ENDERECO_PACIENTE_HISTORICO);
		}
	}

	/**
	 * Método para converter um objeto AinEndPacientesHist em um objeto
	 * AinEnderecosPacientes.
	 * 
	 * @param AinEndPacientesHist
	 * @return AinEnderecosPacientes
	 */
	public AipEndPacientesHist converterEnderecoPacienteEmEndPacienteHist(
			AipEnderecosPacientes endPac) {
		AipEndPacientesHist enderecoPacienteHist = null;

		if (endPac != null) {
			enderecoPacienteHist = new AipEndPacientesHist();
			AipEndPacientesHistId id = new AipEndPacientesHistId();
			enderecoPacienteHist.setId(id);

			// Dados do ID
			id.setPacCodigo(endPac.getId().getPacCodigo());
			id.setSeqp(endPac.getId().getSeqp());

			// Dados restantes
			enderecoPacienteHist.setTipoEndereco(endPac.getTipoEndereco());
			enderecoPacienteHist.setIndPadrao(endPac.getIndPadrao());
			enderecoPacienteHist.setCidade(endPac.getAipCidade());
			enderecoPacienteHist.setUf(endPac.getAipUf());
			enderecoPacienteHist.setLogradouro(endPac.getLogradouro());
			enderecoPacienteHist.setNroLogradouro(endPac.getNroLogradouro());
			enderecoPacienteHist
					.setComplLogradouro(endPac.getComplLogradouro());
			enderecoPacienteHist.setBairro(endPac.getBairro());
			enderecoPacienteHist.setNomeCidade(endPac.getCidade());

			// Alguns registros tem o CEP com o valor 0 no seu histórico de
			// endereço
			enderecoPacienteHist.setCep(endPac.getCep() == null
					|| endPac.getCep() == 0 ? null : endPac.getCep());

			enderecoPacienteHist.setIndExclusaoForcada("".equals(endPac
					.getIndExclusaoForcada())
					|| endPac.getIndExclusaoForcada() == null ? null
					: DominioSimNao.valueOf(endPac.getIndExclusaoForcada()));
			enderecoPacienteHist.setBairroCepLogradouro(endPac
					.getAipBairrosCepLogradouro());
		}

		return enderecoPacienteHist;
	}

	/**
	 * Método para buscar todos os enderecos do paciente armazenados no seu
	 * histórico de endereços. A grande maioria dos pacientes tem somente 1
	 * registro de endereço no seu histórico, porém existem situações onde o
	 * paciente possui mais de um endereço no seu histórico.
	 * 
	 * @return List<AipEndPacientesHist> com todos históricos de endereço do
	 *         paciente
	 */
	public List<AipEndPacientesHist> pesquisarHistoricoEnderecoPaciente(
			Integer codigo) {
		return this.getPacienteFacade().pesquisarHistoricoEnderecoPaciente(codigo);
	}

	/**
	 * Método para converter um objeto AinEndPacientesHist em um objeto
	 * AinEnderecosPacientes.
	 * 
	 * @param AinEndPacientesHist
	 * @return AinEnderecosPacientes
	 */
	public AipEnderecosPacientes converterEnderecoPacienteHistEmEndPaciente(
			AipEndPacientesHist historicoEndPac) {
		AipEnderecosPacientes enderecoPaciente = null;

		if (historicoEndPac != null) {
			enderecoPaciente = new AipEnderecosPacientes();
			AipEnderecosPacientesId id = new AipEnderecosPacientesId();
			enderecoPaciente.setId(id);

			// Dados do ID
			id.setPacCodigo(historicoEndPac.getId().getPacCodigo());
			id.setSeqp(historicoEndPac.getId().getSeqp());

			// Dados restantes
			enderecoPaciente.setTipoEndereco(historicoEndPac.getTipoEndereco());
			enderecoPaciente.setIndPadrao(historicoEndPac.getIndPadrao());
			enderecoPaciente.setAipCidade(historicoEndPac.getCidade());
			enderecoPaciente.setAipUf(historicoEndPac.getUf());
			enderecoPaciente.setLogradouro(historicoEndPac.getLogradouro());
			enderecoPaciente.setNroLogradouro(historicoEndPac
					.getNroLogradouro());
			enderecoPaciente.setComplLogradouro(historicoEndPac
					.getComplLogradouro());
			enderecoPaciente.setBairro(historicoEndPac.getBairro());
			enderecoPaciente.setCidade(historicoEndPac.getNomeCidade());

			// Alguns registros tem o CEP com o valor 0 no seu histórico de
			// endereço
			enderecoPaciente.setCep(historicoEndPac.getCep() == null
					|| historicoEndPac.getCep() == 0 ? null : historicoEndPac
					.getCep());

			enderecoPaciente.setIndExclusaoForcada(historicoEndPac
					.getIndExclusaoForcada() == null ? null : historicoEndPac
					.getIndExclusaoForcada().toString());
			enderecoPaciente.setAipBairrosCepLogradouro(historicoEndPac
					.getBairroCepLogradouro());
		}

		return enderecoPaciente;
	}

	protected IPacienteFacade getPacienteFacade() {
		return (IPacienteFacade) pacienteFacade;
	}

}
