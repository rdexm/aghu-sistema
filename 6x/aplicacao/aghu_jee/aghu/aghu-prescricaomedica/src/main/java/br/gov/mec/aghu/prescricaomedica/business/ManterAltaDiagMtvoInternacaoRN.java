package br.gov.mec.aghu.prescricaomedica.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MpmAltaDiagMtvoInternacao;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.model.MpmCidAtendimento;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaDiagMtvoInternacaoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.StringUtil;

@Stateless
public class ManterAltaDiagMtvoInternacaoRN extends BaseBusiness {


@EJB
private ManterAltaSumarioRN manterAltaSumarioRN;

private static final Log LOG = LogFactory.getLog(ManterAltaDiagMtvoInternacaoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private MpmAltaDiagMtvoInternacaoDAO mpmAltaDiagMtvoInternacaoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -9109583588123789837L;

	public enum ManterAltaDiagMtvoInternacaoRNExceptionCode implements
	BusinessExceptionCode {

		MPM_02600, MPM_02598, MPM_02599, MPM_02601;

	}

	/**
	 * Insere objeto MpmAltaDiagMtvoInternacao.
	 * 
	 * @param {MpmAltaDiagMtvoInternacao} altaDiagMtvoInternacao
	 * @throws ApplicationBusinessException
	 */
	public void inserirAltaDiagMtvoInternacao(MpmAltaDiagMtvoInternacao altaDiagMtvoInternacao) throws ApplicationBusinessException {

		this.preInserirAltaDiagMtvoInternacao(altaDiagMtvoInternacao);
		this.getAltaDiagMtvoInternacaoDAO().persistir(altaDiagMtvoInternacao);
		this.getAltaDiagMtvoInternacaoDAO().flush();

	}

	/**
	 * Atualiza objeto MpmAltaDiagMtvoInternacao.
	 * 
	 * @param {MpmAltaDiagMtvoInternacao} mpmAltaDiagMtvoInternacao
	 * @throws ApplicationBusinessException
	 */
	
	public void atualizarAltaDiagMtvoInternacao(MpmAltaDiagMtvoInternacao altaDiagMtvoInternacao) throws ApplicationBusinessException {

		this.preAtualizarAltaDiagMtvoInternacao(altaDiagMtvoInternacao);
		this.getAltaDiagMtvoInternacaoDAO().atualizar(altaDiagMtvoInternacao);
		this.getAltaDiagMtvoInternacaoDAO().flush();

	}

	/**
	 * Remove objeto MpmAltaDiagMtvoInternacao.
	 * @ORADB MPMT_DMI_BRD
	 * 
	 * @param {MpmAltaDiagMtvoInternacao} altaDiagMtvoInternacao
	 * @throws ApplicationBusinessException
	 */
	public void removerAltaDiagMtvoInternacao(MpmAltaDiagMtvoInternacao altaDiagMtvoInternacao) throws ApplicationBusinessException {

		this.getAltaSumarioRN().verificarAltaSumarioAtivo(altaDiagMtvoInternacao.getAltaSumario());
		this.getAltaDiagMtvoInternacaoDAO().remover(altaDiagMtvoInternacao);
		this.getAltaDiagMtvoInternacaoDAO().flush();

	}

	/**
	 * @ORADB Trigger MPMT_DMI_BASE_BRI ORADB Trigger MPMT_DMI_BRI
	 * 
	 * @param {MpmAltaDiagMtvoInternacao} altaDiagMtvoInternacao
	 * @throws ApplicationBusinessException
	 */
	private void preInserirAltaDiagMtvoInternacao(
			MpmAltaDiagMtvoInternacao altaDiagMtvoInternacao)
	throws ApplicationBusinessException {

		if (altaDiagMtvoInternacao.getCidAtendimento() != null) {
			this.getAltaSumarioRN().verificarCidAtendimento(
					altaDiagMtvoInternacao.getCidAtendimento().getSeq());
		}

		this.getAltaSumarioRN().verificarAltaSumarioAtivo(altaDiagMtvoInternacao.getAltaSumario());

	}

	/**
	 * @ORADB Trigger MPMT_DMI_BASE_BRU ORADB Trigger MPMT_DMI_BRU
	 * 
	 * @param {MpmAltaDiagMtvoInternacao} altaDiagMtvoInternacao
	 * @throws ApplicationBusinessException
	 */
	private void preAtualizarAltaDiagMtvoInternacao(
			MpmAltaDiagMtvoInternacao altaDiagMtvoInternacao)
	throws ApplicationBusinessException {

		MpmAltaDiagMtvoInternacaoDAO dao = this.getAltaDiagMtvoInternacaoDAO();

		MpmAltaDiagMtvoInternacao antigoAltaDiagMtvoInternacao = dao
		.obterMpmAltaDiagMtvoInternacaoVelho(altaDiagMtvoInternacao);

		this.getAltaSumarioRN().verificarAltaSumarioAtivo(
				altaDiagMtvoInternacao.getAltaSumario());

		this.getAltaSumarioRN().verificarSituacao(
				altaDiagMtvoInternacao.getIndSituacao(),
				antigoAltaDiagMtvoInternacao.getIndSituacao(),
				ManterAltaDiagMtvoInternacaoRNExceptionCode.MPM_02600);

		this.verificarAtualizacao(altaDiagMtvoInternacao,
				antigoAltaDiagMtvoInternacao);

		this.getAltaSumarioRN().verificarIndCarga(
				altaDiagMtvoInternacao.getIndCarga(),
				antigoAltaDiagMtvoInternacao.getIndCarga(),
				ManterAltaDiagMtvoInternacaoRNExceptionCode.MPM_02601);

		this.verificarAlteracaoCampos(altaDiagMtvoInternacao,
				antigoAltaDiagMtvoInternacao);

		this.verificarCiaSeq(altaDiagMtvoInternacao,
				antigoAltaDiagMtvoInternacao);

	}

	/**
	 * ORADB Procedure RN_DMIP_VER_UPDATE
	 * 
	 * Só permitir alterar o ind_situacao, se o ind_carga for igual a 'S'.
	 * 
	 * @param {MpmAltaDiagMtvoInternacao} novoAltaDiagMtvoInternacao
	 * @param {MpmAltaDiagMtvoInternacao} antigoAltaDiagMtvoInternacao
	 * @throws ApplicationBusinessException
	 */
	protected void verificarAtualizacao(
			MpmAltaDiagMtvoInternacao novoAltaDiagMtvoInternacao,
			MpmAltaDiagMtvoInternacao antigoAltaDiagMtvoInternacao)
	throws ApplicationBusinessException {

		if (novoAltaDiagMtvoInternacao == null
				|| antigoAltaDiagMtvoInternacao == null) {

			throw new IllegalArgumentException("Parametro invalido!!!");

		}

		Boolean novoCarga = novoAltaDiagMtvoInternacao.getIndCarga();

		if (!novoCarga.booleanValue()
				&& StringUtil.modificado(
						novoAltaDiagMtvoInternacao.getDescCid(),
						antigoAltaDiagMtvoInternacao.getDescCid())
						&& !CoreUtil.modificados(
								novoAltaDiagMtvoInternacao.getCid(),
								antigoAltaDiagMtvoInternacao.getCid())) {

			throw new ApplicationBusinessException(
					ManterAltaDiagMtvoInternacaoRNExceptionCode.MPM_02598);

		}

		if (!novoCarga.booleanValue()
				&& !StringUtil.modificado(
						novoAltaDiagMtvoInternacao.getDescCid(),
						antigoAltaDiagMtvoInternacao.getDescCid())
						&& CoreUtil.modificados(novoAltaDiagMtvoInternacao.getCid(),
								antigoAltaDiagMtvoInternacao.getCid())) {

			throw new ApplicationBusinessException(
					ManterAltaDiagMtvoInternacaoRNExceptionCode.MPM_02599);

		}

	}

	/**
	 * Implementa uma parte da trigger MPMT_DMI_BRU.
	 * 
	 * Não permitir alterar nenhum campo se o ind_carga for verdadeiro.
	 * 
	 * @param {MpmAltaDiagMtvoInternacao} novoAltaDiagMtvoInternacao
	 * @param {MpmAltaDiagMtvoInternacao} antigoAltaDiagMtvoInternacao
	 * @throws ApplicationBusinessException
	 */
	protected void verificarAlteracaoCampos(
			MpmAltaDiagMtvoInternacao novoAltaDiagMtvoInternacao,
			MpmAltaDiagMtvoInternacao antigoAltaDiagMtvoInternacao)
	throws ApplicationBusinessException {

		if (CoreUtil.modificados(novoAltaDiagMtvoInternacao.getId()
				.getAsuApaAtdSeq(), antigoAltaDiagMtvoInternacao.getId()
				.getAsuApaAtdSeq())
				|| CoreUtil.modificados(novoAltaDiagMtvoInternacao.getId()
						.getAsuApaSeq(), antigoAltaDiagMtvoInternacao.getId()
						.getAsuApaSeq())
						|| CoreUtil.modificados(novoAltaDiagMtvoInternacao.getId()
								.getAsuSeqp(), antigoAltaDiagMtvoInternacao.getId()
								.getAsuSeqp())
								|| CoreUtil.modificados(novoAltaDiagMtvoInternacao.getId()
										.getSeqp(), antigoAltaDiagMtvoInternacao.getId()
										.getSeqp())
										|| CoreUtil.modificados(
												novoAltaDiagMtvoInternacao.getDescCid(),
												antigoAltaDiagMtvoInternacao.getDescCid())
												|| CoreUtil.modificados(
														novoAltaDiagMtvoInternacao.getComplCid() == null ? "" : novoAltaDiagMtvoInternacao.getComplCid(),
														antigoAltaDiagMtvoInternacao.getComplCid() == null ? "" : antigoAltaDiagMtvoInternacao.getComplCid())
														|| CoreUtil.modificados(novoAltaDiagMtvoInternacao.getCid(),
																antigoAltaDiagMtvoInternacao.getCid())) {

			if (novoAltaDiagMtvoInternacao.getIndCarga().booleanValue()) {

				throw new ApplicationBusinessException(
						ManterAltaDiagMtvoInternacaoRNExceptionCode.MPM_02601);

			}

		}

	}

	/**
	 * ORADB Trigger MPMT_DMI_BASE_BRU.
	 * 
	 * Se novo cia_seq for diferente do antigo cia_seq, então verifica
	 * integridade com mpm_cid_atendimentos.
	 * 
	 * @param {MpmAltaDiagMtvoInternacao} novoAltaDiagMtvoInternacao
	 * @param {MpmAltaDiagMtvoInternacao} antigoAltaDiagMtvoInternacao
	 * @throws ApplicationBusinessException
	 */
	protected void verificarCiaSeq(
			MpmAltaDiagMtvoInternacao novoAltaDiagMtvoInternacao,
			MpmAltaDiagMtvoInternacao antigoAltaDiagMtvoInternacao)
	throws ApplicationBusinessException {

		MpmCidAtendimento cidAtendimento = novoAltaDiagMtvoInternacao.getCidAtendimento();
		if (cidAtendimento != null && !cidAtendimento.equals(
				antigoAltaDiagMtvoInternacao.getCidAtendimento())) {

			this.getAltaSumarioRN().verificarCidAtendimento(
					cidAtendimento.getSeq());

		}

	}
	
	/**
	 * Método que verifica a validação
	 * do diagnóstico de mtvo, da internação 
	 * da alta do paciente. Deve pelo menos 
	 * ter um registro ativo associado ao 
	 * sumário do paciente.
	 * 
	 * @author gfmenezes
	 * 
	 * @param altaSumarioId
	 * @return
	 */
	public Boolean validarAltaDiagMtvoInternacao(MpmAltaSumarioId altaSumarioId) {
		List<Long> result = this.
			getAltaDiagMtvoInternacaoDAO().
			listAltaDiagMtvoInternacao(altaSumarioId);
		
		Long rowCount = 0L;
        if (!result.isEmpty()) {
            rowCount = (Long) result.get(0);
        }
        
		return rowCount > 0;
	}

	protected ManterAltaSumarioRN getAltaSumarioRN() {
		return manterAltaSumarioRN;
	}

	protected MpmAltaDiagMtvoInternacaoDAO getAltaDiagMtvoInternacaoDAO() {
		return mpmAltaDiagMtvoInternacaoDAO;
	}

}
