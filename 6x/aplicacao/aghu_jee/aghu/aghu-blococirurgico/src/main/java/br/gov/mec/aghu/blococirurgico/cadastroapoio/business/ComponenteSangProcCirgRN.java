package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcCompSangProcCirgDAO;
import br.gov.mec.aghu.model.AbsComponenteSanguineo;
import br.gov.mec.aghu.model.MbcCompSangProcCirg;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ComponenteSangProcCirgRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(ComponenteSangProcCirgRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcCompSangProcCirgDAO mbcCompSangProcCirgDAO;


	/**
	 * 
	 */
	private static final long serialVersionUID = 5024524897490874728L;
	
	public enum ComponenteSangProcCirgRNExceptionCode implements BusinessExceptionCode {
		MBC_00567, ERRO_COMPONENTE_SANG_PROC_CIRG_DUPLICADO;
	}
	
	public void persistir(MbcCompSangProcCirg componente, Boolean inserir) throws BaseException {
		if (inserir) {
			this.inserir(componente);
		}
		else {
			this.atualizar(componente);
		}
	}
	
	public void remover(MbcCompSangProcCirg componente) {
		MbcCompSangProcCirg c = getMbcCompSangProcCirgDAO().obterPorChavePrimaria(componente.getSeq());
		if (c != null) {
			getMbcCompSangProcCirgDAO().remover(c);
		}
	}
	
	public void inserir(MbcCompSangProcCirg componente) throws BaseException {
		this.verificarCompSangProcCirgDuplicado(componente);
		this.validarQtdUnidadeouQtdMl(componente);
		this.preInserir(componente);
		this.getMbcCompSangProcCirgDAO().persistir(componente);
	}
	
	public void atualizar(MbcCompSangProcCirg componente) throws BaseException {
		this.validarQtdUnidadeouQtdMl(componente);
		this.preAtualizar(componente);
		this.getMbcCompSangProcCirgDAO().atualizar(componente);
	}
	
	/**
	 * ORADB: MBCT_CPC_BRI
	 * 
	 * @param componente
	 * @param servidorLogado
	 */
	private void preInserir(MbcCompSangProcCirg componente) {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		componente.setCriadoEm(new Date());
		componente.setRapServidores(servidorLogado);
	}

	/**
	 * ORADB: MBCT_CPC_BRU
	 * 
	 * @param componente
	 * @param servidorLogado
	 */
	private void preAtualizar(MbcCompSangProcCirg componente) {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		componente.setCriadoEm(new Date());
		componente.setRapServidores(servidorLogado);
	}

	private void validarQtdUnidadeouQtdMl(MbcCompSangProcCirg componente) throws BaseException {
		if(!(((componente.getQtdeMl() != null && componente.getQtdeMl() > 0) && componente.getQtdeUnidade() == null) || (componente.getQtdeMl() == null && (componente.getQtdeUnidade() != null && componente.getQtdeUnidade() > 0)))) {
			throw new ApplicationBusinessException(ComponenteSangProcCirgRNExceptionCode.MBC_00567);
		}
	}
	
	private void verificarCompSangProcCirgDuplicado(MbcCompSangProcCirg componente)
			throws ApplicationBusinessException {
		
		MbcProcedimentoCirurgicos procedimentoCirurgico = componente.getMbcProcedimentoCirurgicos();
		AbsComponenteSanguineo componenteSanguineo = componente.getAbsComponenteSanguineo();

		Long countAssocProcCirgCompSang = getMbcCompSangProcCirgDAO()
				.obterCountCompSangPorPciSeqECodigoCompSang(
						procedimentoCirurgico.getSeq(),
						componenteSanguineo.getCodigo());

		if (countAssocProcCirgCompSang > 0) {
			throw new ApplicationBusinessException(
					ComponenteSangProcCirgRNExceptionCode.ERRO_COMPONENTE_SANG_PROC_CIRG_DUPLICADO,
					componenteSanguineo.getDescricao(), procedimentoCirurgico.getDescricao());
		}
	}
	
	protected MbcCompSangProcCirgDAO getMbcCompSangProcCirgDAO() {
		return mbcCompSangProcCirgDAO;
	}

}
