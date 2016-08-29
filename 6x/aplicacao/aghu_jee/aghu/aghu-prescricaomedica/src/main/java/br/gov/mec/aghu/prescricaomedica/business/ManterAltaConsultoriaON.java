package br.gov.mec.aghu.prescricaomedica.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MpmAltaConsultoria;
import br.gov.mec.aghu.model.MpmAltaConsultoriaId;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmSolicitacaoConsultoria;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaConsultoriaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaSumarioDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmSolicitacaoConsultoriaDAO;
import br.gov.mec.aghu.prescricaomedica.vo.SumarioAltaProcedimentosConsultoriasVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 *
 * @author lalegre
 *
 */
@Stateless
public class ManterAltaConsultoriaON extends BaseBusiness {


@EJB
private ManterAltaConsultoriaRN manterAltaConsultoriaRN;

private static final Log LOG = LogFactory.getLog(ManterAltaConsultoriaON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private MpmAltaSumarioDAO mpmAltaSumarioDAO;

@Inject
private MpmAltaConsultoriaDAO mpmAltaConsultoriaDAO;

@EJB
private IAghuFacade aghuFacade;

@Inject
private MpmSolicitacaoConsultoriaDAO mpmSolicitacaoConsultoriaDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -3873477110672772875L;

	public enum ManterAltaConsultoriaONExceptionCode implements
			BusinessExceptionCode {

		DATA_OBRIGATORIA, ESPECIALIDADE_OBRIGATORIA;

		public void throwException() throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this);
		}

	}

	/**
	 * Gera novo MPM_ALTA_CONSULTORIAS para alta sumários
	 * @param altaSumario
	 * @throws BaseException 
	 */
	public void gerarAltaConsultoria(MpmAltaSumario altaSumario) throws BaseException {

		Integer altanAtdSeq = altaSumario.getId().getApaAtdSeq();
		Integer altanApaSeq = altaSumario.getId().getApaSeq();
		List<MpmSolicitacaoConsultoria> listaSolicitacaoConsultoria = this.getMpmSolicitacaoConsultoriaDAO().buscaSolicitacaoConsultoriaSumarioAlta(altanAtdSeq);

		for (MpmSolicitacaoConsultoria solicitacaoConsultoria : listaSolicitacaoConsultoria) {

			if (!this.getMpmAltaConsultoriaDAO().possuiAltaConsultoria(altanAtdSeq, altanApaSeq)) {

				MpmAltaConsultoria altaConsultoria = new MpmAltaConsultoria();

				altaConsultoria.setAltaSumario(altaSumario);
				altaConsultoria.setIndSituacao(DominioSituacao.A);
				altaConsultoria.setIndCarga(true);
				altaConsultoria.setDthrConsultoria(solicitacaoConsultoria.getDthrResposta());
				String nomeEspecialidade = WordUtils.capitalize(solicitacaoConsultoria.getEspecialidade().getNomeEspecialidade());
				altaConsultoria.setDescConsultoria(nomeEspecialidade);
				altaConsultoria.setSolicitacaoConsultoria(solicitacaoConsultoria);
				altaConsultoria.setAghEspecialidade(solicitacaoConsultoria.getEspecialidade());
				this.getMpmAltaConsultoriaDAO().desatachar(altaConsultoria);
				this.getManterAltaConsultoriaRN().inserirAltaConsultoria(altaConsultoria);

			}

		}

	}

	/**
	 * Atualiza consultorias do sumario ativo
	 * @param altanAtdSeq
	 * @param altanApaSeq
	 * @param altanAsuSeqp
	 * @param novoSeqp
	 * @throws BaseException 
	 */
	public void versionarAltaConsultoria(MpmAltaSumario altaSumario, Short antigoAsuSeqp) throws ApplicationBusinessException {

		List<MpmAltaConsultoria> lista = this.getMpmAltaConsultoriaDAO().obterMpmAltaConsultoria(altaSumario.getId().getApaAtdSeq(), altaSumario.getId().getApaSeq(), antigoAsuSeqp);

		for (MpmAltaConsultoria altaConsultoria : lista) {

			MpmAltaConsultoria novoAltaConsultoria = new MpmAltaConsultoria();
			novoAltaConsultoria.setAltaSumario(altaSumario);
			novoAltaConsultoria.setAghEspecialidade(altaConsultoria.getAghEspecialidade());
			novoAltaConsultoria.setDescConsultoria(altaConsultoria.getDescConsultoria());
			novoAltaConsultoria.setDthrConsultoria(altaConsultoria.getDthrConsultoria());
			novoAltaConsultoria.setIndCarga(altaConsultoria.getIndCarga());
			novoAltaConsultoria.setIndImprimeResposta(altaConsultoria.getIndImprimeResposta());
			novoAltaConsultoria.setIndSituacao(altaConsultoria.getIndSituacao());
			novoAltaConsultoria.setSolicitacaoConsultoria(altaConsultoria.getSolicitacaoConsultoria());
			this.getManterAltaConsultoriaRN().inserirAltaConsultoria(novoAltaConsultoria);

		}
		
	}

	public void inserirAltaConsultoria(SumarioAltaProcedimentosConsultoriasVO vo, Date dataConsultoria) throws ApplicationBusinessException {
		Short seqEspecialidade = vo.getSeqEspecialidade();

		if (dataConsultoria == null) {
			ManterAltaConsultoriaONExceptionCode.DATA_OBRIGATORIA.throwException();
		}
		if (seqEspecialidade == null) {
			ManterAltaConsultoriaONExceptionCode.ESPECIALIDADE_OBRIGATORIA.throwException();
		}

		MpmAltaSumario altaSumario = this.getMpmAltaSumarioDAO().obterPorChavePrimaria(vo.getId());
		AghEspecialidades especialidade = this.getAghuFacade().obterAghEspecialidadesPorChavePrimaria(seqEspecialidade);

		MpmSolicitacaoConsultoria solicitacaoConsultoria = null;
		if (vo.getSolicitacaoConsultoriaId() != null) {
			solicitacaoConsultoria = this.getMpmSolicitacaoConsultoriaDAO().obterPorChavePrimaria(vo.getSolicitacaoConsultoriaId());
		}

		MpmAltaConsultoriaId id = new MpmAltaConsultoriaId();
		id.setAsuApaAtdSeq(vo.getId().getApaAtdSeq());
		id.setAsuApaSeq(vo.getId().getApaSeq());
		id.setAsuSeqp(vo.getId().getSeqp());

		MpmAltaConsultoria altaConsultoria = new MpmAltaConsultoria();
		altaConsultoria.setId(id);
		altaConsultoria.setAltaSumario(altaSumario);
		altaConsultoria.setIndSituacao(DominioSituacao.A);
		altaConsultoria.setIndCarga(vo.getOrigemListaCombo());
		altaConsultoria.setAghEspecialidade(especialidade);
		altaConsultoria.setDthrConsultoria(dataConsultoria);
		altaConsultoria.setDescConsultoria(vo.getDescricao());
		altaConsultoria.setSolicitacaoConsultoria(solicitacaoConsultoria);

		this.getManterAltaConsultoriaRN().inserirAltaConsultoria(altaConsultoria);
	}

	public void alterarAltaConsultoria(SumarioAltaProcedimentosConsultoriasVO vo, Date dataConsultoria) throws ApplicationBusinessException {

		if (dataConsultoria == null) {
			ManterAltaConsultoriaONExceptionCode.DATA_OBRIGATORIA.throwException();
		}

		MpmAltaConsultoriaId id = new MpmAltaConsultoriaId();
		id.setAsuApaAtdSeq(vo.getId().getApaAtdSeq());
		id.setAsuApaSeq(vo.getId().getApaSeq());
		id.setAsuSeqp(vo.getId().getSeqp());
		id.setSeqp(vo.getSeqp());

		MpmAltaConsultoria altaConsultoria = this.getMpmAltaConsultoriaDAO().obterPorChavePrimaria(id);
		altaConsultoria.setDthrConsultoria(dataConsultoria);

		this.getManterAltaConsultoriaRN().atualizarAltaConsultoria(altaConsultoria);
	}

	public void removerAltaConsultoria(SumarioAltaProcedimentosConsultoriasVO vo) throws ApplicationBusinessException {
		MpmAltaConsultoriaId id = new MpmAltaConsultoriaId();
		id.setAsuApaAtdSeq(vo.getId().getApaAtdSeq());
		id.setAsuApaSeq(vo.getId().getApaSeq());
		id.setAsuSeqp(vo.getId().getSeqp());
		id.setSeqp(vo.getSeqp());

		MpmAltaConsultoria altaConsultoria = this.getMpmAltaConsultoriaDAO().obterPorChavePrimaria(id);
		altaConsultoria.setIndSituacao(DominioSituacao.I);

		this.getManterAltaConsultoriaRN().atualizarAltaConsultoria(altaConsultoria);
	}
	
	/**
	 * Remove consultorias do sumario.
	 * @param altaSumario
	 * @throws ApplicationBusinessException
	 */
	public void removerAltaConsultoria(MpmAltaSumario altaSumario) throws ApplicationBusinessException {
		List<MpmAltaConsultoria> lista = this.getMpmAltaConsultoriaDAO().obterMpmAltaConsultoria(
				altaSumario.getId().getApaAtdSeq(), 
				altaSumario.getId().getApaSeq(), 
				altaSumario.getId().getSeqp(),
				false
		);

		for (MpmAltaConsultoria altaConsultoria : lista) {
			this.getManterAltaConsultoriaRN().removerAltaConsultoria(altaConsultoria);
		}
	}

	protected MpmSolicitacaoConsultoriaDAO getMpmSolicitacaoConsultoriaDAO() {
		return mpmSolicitacaoConsultoriaDAO;
	}

	protected ManterAltaConsultoriaRN getManterAltaConsultoriaRN() {
		return manterAltaConsultoriaRN;
	}

	protected MpmAltaConsultoriaDAO getMpmAltaConsultoriaDAO() {
		return mpmAltaConsultoriaDAO;
	}


	private MpmAltaSumarioDAO getMpmAltaSumarioDAO() {
		return mpmAltaSumarioDAO;
	}

	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

}
