package br.gov.mec.aghu.exameselaudos.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelPacUnidFuncionaisDAO;
import br.gov.mec.aghu.exames.dao.AelProjetoIntercorrenciaInternacaoDAO;
import br.gov.mec.aghu.exames.dao.AelProjetoPesquisasDAO;
import br.gov.mec.aghu.exames.dao.AelProtocoloInternoUnidsDAO;
import br.gov.mec.aghu.model.AelPacUnidFuncionais;
import br.gov.mec.aghu.model.AelProjetoIntercorrenciaInternacao;
import br.gov.mec.aghu.model.AelProjetoPesquisas;
import br.gov.mec.aghu.model.AelProtocoloInternoUnids;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class ApoioEmissaoLaudosON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ApoioEmissaoLaudosON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelProjetoPesquisasDAO aelProjetoPesquisasDAO;

@Inject
private AelPacUnidFuncionaisDAO aelPacUnidFuncionaisDAO;

@Inject
private AelProjetoIntercorrenciaInternacaoDAO aelProjetoIntercorrenciaInternacaoDAO;

@Inject
private AelProtocoloInternoUnidsDAO aelProtocoloInternoUnidsDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -1553116140155858451L;

	/**
	 * Método para percorrer todos os protocolos referentes ao paciente recebido
	 * por parametro. Com base nesses protocolos são excluidos objetos de
	 * AelProtocoloInternoUnids e AelProtocoloInternoUnids.
	 * 
	 * @param código
	 *            do paciente
	 * @throws ApplicationBusinessException
	 */	
	public void removerProtocolosNeurologia(Integer codigoPaciente)
			throws ApplicationBusinessException {

		List<AelProtocoloInternoUnids> protocoloInternoList = this
				.pesquisarProtocolosPorPaciente(codigoPaciente);
		AipPacientes pacienteAux;

		List<AelPacUnidFuncionais> unidadeFuncionalPacienteList = new ArrayList<AelPacUnidFuncionais>(
				0);
		Short unidadeFuncionalPacienteSeq = null;

		for (AelProtocoloInternoUnids protocoloInterno : protocoloInternoList) {
			// Seta campos usados na restrição do método
			// pesquisarUnidadesFuncionaisPaciente()
			pacienteAux = new AipPacientes();
			pacienteAux.setCodigo(protocoloInterno.getId().getPacCodigo());
			pacienteAux.setUnidadeFuncional(protocoloInterno.getId()
					.getUnidadeFuncional());

			if (pacienteAux.getUnidadeFuncional() == null) {
				unidadeFuncionalPacienteSeq = null;
			} else {
				unidadeFuncionalPacienteSeq = pacienteAux.getUnidadeFuncional()
						.getSeq();
			}

			unidadeFuncionalPacienteList = this
					.pesquisarUnidadesFuncionaisPaciente(
							pacienteAux.getCodigo(),
							unidadeFuncionalPacienteSeq);

			for (AelPacUnidFuncionais unidadeFuncionalPaciente : unidadeFuncionalPacienteList) {
				this.getAelPacUnidFuncionaisDAO().remover(
						unidadeFuncionalPaciente);
				this.getAelPacUnidFuncionaisDAO().flush();
			}
		}

		for (AelProtocoloInternoUnids protocoloInterno : this
				.pesquisarProtocolosPorPaciente(codigoPaciente)) {
			this.getAelProtocoloInternoUnidsDAO().remover(protocoloInterno);
			this.getAelProtocoloInternoUnidsDAO().flush();
		}
	}

	/**
	 * Método para pesquisar todos os protocolos na classe
	 * AelProtocoloInternoUnids para um determinado paciente.
	 * 
	 * @param paciente
	 * @return Lista de objetos AelPacUnidFuncionais
	 */
	public List<AelPacUnidFuncionais> pesquisarUnidadesFuncionaisPaciente(
			Integer codigoPaciente, Short sequenciaUnidadeFuncional) {

		return getAelPacUnidFuncionaisDAO()
				.pesquisarUnidadesFuncionaisPaciente(codigoPaciente,
						sequenciaUnidadeFuncional);
	}

	/**
	 * Método para pesquisar todos os protocolos na classe
	 * AelProtocoloInternoUnids para um determinado paciente.
	 * 
	 * @param paciente
	 * @return Lista de objetos AelProtocoloInternoUnids
	 */
	public List<AelProtocoloInternoUnids> pesquisarProtocolosPorPaciente(
			Integer codigoPaciente) {

		return this.getAelProtocoloInternoUnidsDAO()
				.pesquisarProtocolosPorPaciente(codigoPaciente);
	}

	/**
	 * Método para pesquisar todos protocolos na classe AelProtocoloInternoUnids
	 * para um determinado paciente e retornar os mesmo em uma string com os
	 * valores separados por vírgula.
	 * 
	 * @param paciente
	 * @return String com numero dos protolos separados por vírgulas.
	 */
	public String pesquisarProtocolosPorPacienteString(Integer codigoPaciente) {

		StringBuffer retorno = new StringBuffer();

		List<AelProtocoloInternoUnids> protocolosList = this
				.pesquisarProtocolosPorPaciente(codigoPaciente);
		for (AelProtocoloInternoUnids protocolo : protocolosList) {
			if (retorno.length() == 0 && protocolo.getNroProtocolo() != null) {
				retorno.append(protocolo.getNroProtocolo());
			} else if (retorno.length() > 0 && protocolo.getNroProtocolo() != null) {
				retorno.append(", ").append(protocolo.getNroProtocolo());
			}
		}

		return retorno.toString();
	}

	/**
	 * Método para buscar projeto de pesquisa pelo seu ID.
	 * 
	 * @param seq
	 * @return
	 */
	public AelProjetoPesquisas obterProjetoPesquisa(Integer seq) {
		return this.getAelProjetoPesquisasDAO().obterPorChavePrimaria(seq);
	}

	/**
	 * Método para obter AelProjetoIntercorrenciaInternacao pelo seu ID.
	 * 
	 * @param codigoPaciente
	 * @param seqProjetoPesquisa
	 * @param seq
	 * @return
	 */
	public AelProjetoIntercorrenciaInternacao obterProjetoIntercorrenciaInternacao(
			Integer codigoPaciente, Integer seqProjetoPesquisa, Short seq) {

		return getAelProjetoIntercorrenciaInternacaoDAO()
				.obterProjetoIntercorrenciaInternacao(codigoPaciente,
						seqProjetoPesquisa, seq);
	}
	
	/**
	 * Método para persistir AelProjetoIntercorrenciaInternacao
	 * 
	 * @param proejtoIntercorrenciaInternacao
	 */
	public void persistirProjetoIntercorrenciaInternacao(
			AelProjetoIntercorrenciaInternacao projetoIntercorrenciaInternacao) {

		// TODO implementar triggers de AelProjeotIntercorrenciaInternacao

	}
	
	protected AelProtocoloInternoUnidsDAO getAelProtocoloInternoUnidsDAO() {
		return aelProtocoloInternoUnidsDAO;
	}

	protected AelPacUnidFuncionaisDAO getAelPacUnidFuncionaisDAO() {
		return aelPacUnidFuncionaisDAO;
	}

	protected AelProjetoPesquisasDAO getAelProjetoPesquisasDAO() {
		return aelProjetoPesquisasDAO;
	}

	protected AelProjetoIntercorrenciaInternacaoDAO getAelProjetoIntercorrenciaInternacaoDAO() {
		return aelProjetoIntercorrenciaInternacaoDAO;
	}

}
