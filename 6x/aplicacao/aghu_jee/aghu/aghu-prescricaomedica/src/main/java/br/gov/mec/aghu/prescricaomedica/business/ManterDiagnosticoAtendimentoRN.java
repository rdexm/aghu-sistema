package br.gov.mec.aghu.prescricaomedica.business;

import java.util.Calendar;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSexoDeterminante;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MpmCidAtendimento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.dao.MpmCidAtendimentoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ManterDiagnosticoAtendimentoRN extends BaseBusiness {

private static final String INSERIDO = "inserido";

private static final Log LOG = LogFactory.getLog(ManterDiagnosticoAtendimentoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private MpmCidAtendimentoDAO mpmCidAtendimentoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 8317300259564753103L;

	public enum DiagnosticoAtendimentoRNExceptionCode implements
			BusinessExceptionCode {
		MENSAGEM_CID_DEVE_ESTAR_ATIVO, MENSAGEM_CID_SEXO_INCOMPATIVEL, MENSAGEM_CID_SERVIDOR_NAO_CADASTRADO
	}

	

	/**
	 * Insere um novo registro de atendimento.
	 * 
	 * @param atendimento
	 *            Atendimento a ser inserido.
	 */
	public void insert(MpmCidAtendimento atendimento, AipPacientes paciente,
			RapServidores servidor) throws ApplicationBusinessException {

		this.beforeRowInsert(atendimento, paciente, servidor);
	}

	/**
	 * Atualiza um novo registro de atendimento.
	 * 
	 * @param atendimento
	 *            Atendimento a ser atualizado.
	 */
	public void update(MpmCidAtendimento atendimento, RapServidores servidor)
			throws ApplicationBusinessException {

		this.beforeRowUpdate(atendimento, servidor);
	}

	public void delete(MpmCidAtendimento atendimento, RapServidores servidor)
			throws ApplicationBusinessException {

		this.beforeRowDelete(atendimento, servidor);
	}

	/**
	 * Regra para executar antes de inserir novo registro.
	 * 
	 * @param atendimento
	 *            Atendimento a ser inserido.
	 */
	private void beforeRowInsert(MpmCidAtendimento atendimento,
			AipPacientes paciente, RapServidores servidor)
			throws ApplicationBusinessException {

		// RN01
		atendimento.setCriadoEm(Calendar.getInstance().getTime());

		// RN02
		verificarStatusCid(atendimento.getCid());

		// RN03
		verificarSexoCompativel(atendimento, paciente);

		// RN04
		popularServidor(atendimento, servidor);
	
	}

	/**
	 * Regra para executar antes de Atualizar um registro.
	 * 
	 * @param atendimento
	 */
	private void beforeRowUpdate(MpmCidAtendimento atendimento,
			RapServidores servidor) throws ApplicationBusinessException {

		// RN07
		atendimento.setDthrFim(Calendar.getInstance().getTime());
		popularServidorMovimentado(atendimento, servidor);

		// D03
		// não deixa alterar o complemento do registro. Ao invés disso,
		// encerra o registro atual (colocando
		// data de fim) e utiliza o complemento que está no banco. Lá na
		// controller, chama a insert para
		// gerar um novo registro com o novo complemento.

		MpmCidAtendimento antigoAtd = this.getMpmCidAtendimentoDAO()
				.obterCidAtendimentoPeloId(atendimento.getSeq());

		atendimento.setComplemento(antigoAtd.getComplemento());
		atendimento.setAltCidPaciente(false);
	
	}

	/**
	 * Tratamento para excluir um registro
	 * 
	 * @param atendimento
	 * @param servidor
	 * @throws ApplicationBusinessException
	 */
	private void beforeRowDelete(MpmCidAtendimento atendimento,
			RapServidores servidor) throws ApplicationBusinessException {

		// RN07
		atendimento.setDthrFim(Calendar.getInstance().getTime());
		popularServidorMovimentado(atendimento, servidor);

		// D03
		atendimento.setAltCidPaciente(false);
		
	}

	/**
	 * Verifica se status de um determinado CID está ou não ativo
	 * 
	 * @param atendimento
	 * @throws CidAtivoException
	 * @throws ApplicationBusinessException
	 */
	public void verificarStatusCid(AghCid cid) throws ApplicationBusinessException {

		if (!cid.getSituacao().equals(DominioSituacao.A)) {
			throw new ApplicationBusinessException(
					DiagnosticoAtendimentoRNExceptionCode.MENSAGEM_CID_DEVE_ESTAR_ATIVO,
					INSERIDO);
		}
	}

	/**
	 * Verifica se o sexo do CID é compatível com o sexo do paciente.
	 * 
	 * @param cid
	 * @throws CidSexoIncompativel
	 * @throws ApplicationBusinessException
	 */
	public void verificarSexoCompativel(MpmCidAtendimento atendimento,
			AipPacientes paciente) throws 
			ApplicationBusinessException {

		// se o campo "restrição" for "Q" não faz comparação de sexo entre
		// paciente e CID.
		if (atendimento.getCid().getRestricaoSexo()
				.equals(DominioSexoDeterminante.Q)) {
			return;
		}

		// obtem o paciente do atendimento.
		if ((paciente.getSexo() != null)
				&& (!paciente.getSexo().name()
						.equals(atendimento.getCid().getRestricaoSexo().name()))) {
			throw new ApplicationBusinessException(
					DiagnosticoAtendimentoRNExceptionCode.MENSAGEM_CID_SEXO_INCOMPATIVEL,
					INSERIDO);
		}
	}

	/**
	 * Popula os dados de servidor informado para dentro de atendimento.
	 * 
	 * @param atendimento
	 * @param servidor
	 * @throws CidServidorNaoCadastradoException
	 * @throws ApplicationBusinessException
	 */
	public void popularServidor(MpmCidAtendimento atendimento,
			RapServidores servidor) throws	ApplicationBusinessException {

		if (servidor == null) {
			throw new ApplicationBusinessException(
					DiagnosticoAtendimentoRNExceptionCode.MENSAGEM_CID_SERVIDOR_NAO_CADASTRADO,
					INSERIDO);
		}

		// popula com os dados do servidor movimentado.
		atendimento.setServidor(servidor);
	}

	public void popularServidorMovimentado(MpmCidAtendimento atendimento,
			RapServidores servidor) throws ApplicationBusinessException {

		if (servidor == null) {
			throw new ApplicationBusinessException(
					DiagnosticoAtendimentoRNExceptionCode.MENSAGEM_CID_SERVIDOR_NAO_CADASTRADO,
					INSERIDO);
		}

		// popula com os dados do servidor movimentado.
		atendimento.setServidorMovimentado(servidor);
	}

	protected MpmCidAtendimentoDAO getMpmCidAtendimentoDAO() {
		return mpmCidAtendimentoDAO;
	}
}
