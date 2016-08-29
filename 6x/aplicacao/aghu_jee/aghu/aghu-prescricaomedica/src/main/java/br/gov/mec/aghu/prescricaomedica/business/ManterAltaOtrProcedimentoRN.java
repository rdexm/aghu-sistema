package br.gov.mec.aghu.prescricaomedica.business;


import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MpmAltaOtrProcedimento;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaOtrProcedimentoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ManterAltaOtrProcedimentoRN extends BaseBusiness {


@EJB
private ManterAltaSumarioRN manterAltaSumarioRN;

private static final Log LOG = LogFactory.getLog(ManterAltaOtrProcedimentoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private MpmAltaOtrProcedimentoDAO mpmAltaOtrProcedimentoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 3826546280738919692L;

	public enum ManterAltaOtrProcedimentoRNExceptionCode implements
	BusinessExceptionCode {

		MPM_02631, //
		MPM_02632, //
		MPM_02874, // 
		MPM_VALIDA_DT_OUTROS_PROCED, //
		MPM_02875, //
		ERRO_INSERIR_ALTA_OTR_PROCEDIMENTO, //
		MPM_02629, //
		MPM_02630, //
		MPM_02627, // 
		MPM_02628, //
		MPM_02660, //
		MPM_02818;

		public void throwException(Object... params)
		throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}

		public void throwException(Throwable cause, Object... params)
		throws ApplicationBusinessException {
			// Tratamento adicional para não esconder a excecao de negocio
			// original
			if (cause instanceof ApplicationBusinessException) {
				throw (ApplicationBusinessException) cause;
			}
			throw new ApplicationBusinessException(this, cause, params);
		}

	}

	/**
	 * Insere objeto MpmAltaOtrProcedimento.
	 * 
	 * @param {MpmAltaOtrProcedimento} altaOtrProcedimento
	 * @throws ApplicationBusinessException
	 */
	public void inserirAltaOtrProcedimento(
			MpmAltaOtrProcedimento altaOtrProcedimento)
	throws ApplicationBusinessException {

		try {

			this.preInserirAltaOtrProcedimento(altaOtrProcedimento);
			this.getAltaOtrProcedimentoDAO().persistir(altaOtrProcedimento);
			this.getAltaOtrProcedimentoDAO().flush();

		} catch (Exception e) {
			logError(e.getMessage(), e);
			ManterAltaOtrProcedimentoRNExceptionCode.ERRO_INSERIR_ALTA_OTR_PROCEDIMENTO
			.throwException(e);

		}

	}

	/**
	 * ORADB Trigger MPMT_OPC_BRI
	 * 
	 * @param {MpmAltaOtrProcedimento} altaOtrProcedimento
	 * @throws ApplicationBusinessException
	 */
	protected void preInserirAltaOtrProcedimento(
			MpmAltaOtrProcedimento altaOtrProcedimento) throws ApplicationBusinessException {

		Integer pciSeq = null;
		Short pedSeq = null;

		if (altaOtrProcedimento.getMbcProcedimentoCirurgicos() != null) {

			pciSeq = altaOtrProcedimento.getMbcProcedimentoCirurgicos().getSeq();

		}

		if (altaOtrProcedimento.getMpmProcedEspecialDiversos() != null) {

			pedSeq = altaOtrProcedimento.getMpmProcedEspecialDiversos().getSeq();

		}

		// Verifica se ALTA_SUMARIOS está ativo
		this.getAltaSumarioRN().verificarAltaSumarioAtivo(altaOtrProcedimento.getMpmAltaSumarios());

		// Verifica se precisa preencher o compl_out_procedimento
		Integer matCodigo = altaOtrProcedimento.getMatCodigo() != null ? altaOtrProcedimento.getMatCodigo().getCodigo() : null;
		this.verificarCompl(
				altaOtrProcedimento.getComplOutProcedimento(),
				altaOtrProcedimento.getIndCarga(),
				pciSeq,
				pedSeq,
				matCodigo);

		// Verifica data de procedimento
		this.getAltaSumarioRN().verificarDtHr(altaOtrProcedimento.getMpmAltaSumarios().getId().getApaAtdSeq(), altaOtrProcedimento.getDthrOutProcedimento());

	}

	/**
	 * ORADB Procedure MPMK_OPC_RN.RN_OPCP_VER_COMPL.
	 * 
	 * Operação: INS e UPD Descrição: O atributo compl out procedimento deve ser
	 * nulo quando ind_carga = 'S' ou quando uma das fk's ( PROCEDIMENTO
	 * CIRURGICO ou PROCED ESPECIAL DIVERSO ) estiverem preenchidas Se nenhuma
	 * das FK's (PROCEDIMENTO CIRURGICO, PROCED ESPECIAL DIVERSO, MATERIAL)
	 * estiver preenchida, o atributo compl out procedimento deverá ser
	 * obrigatoriamente informado sumário estiver ativo
	 * 
	 * bsoliveira - 28/10/2010
	 * 
	 * @param {String} complOutProcedimento de MpmAltaOtrProcedimento.
	 * @param {Boolean} indCarga de MpmAltaOtrProcedimento.
	 * @param {Integer} novoPciSeq seq de MbcProcedimentoCirurgicos.
	 * @param {Short} novoPedSeq seq de MpmProcedEspecialDiversos.
	 * @param {Integer} novoMatCodigo codigo de ScoMateriais.
	 * @throws ApplicationBusinessException
	 */
	public void verificarCompl(String complOutProcedimento, Boolean indCarga,
			Integer novoPciSeq, Short novoPedSeq, Integer novoMatCodigo)
	throws ApplicationBusinessException {

		if ((complOutProcedimento != null)
				&& (((indCarga != null) && indCarga.booleanValue())
						|| (novoPciSeq != null) || (novoPedSeq != null) || (novoMatCodigo != null))) {

			ManterAltaOtrProcedimentoRNExceptionCode.MPM_02631.throwException();

		}

		if (((indCarga != null) && indCarga.booleanValue()) && (novoPciSeq == null)
				&& (novoPedSeq == null) && (novoMatCodigo == null)) {

			ManterAltaOtrProcedimentoRNExceptionCode.MPM_02632.throwException();

		}

	}

	protected void preAtualizarAltaOtrProcedimento(
			MpmAltaOtrProcedimento altaOtrProcedimento) throws ApplicationBusinessException {

		Integer pciSeq = null;
		Short pedSeq = null;

		if (altaOtrProcedimento.getMbcProcedimentoCirurgicos() != null) {

			pciSeq = altaOtrProcedimento.getMbcProcedimentoCirurgicos().getSeq();

		}

		if (altaOtrProcedimento.getMpmProcedEspecialDiversos() != null) {

			pedSeq = altaOtrProcedimento.getMpmProcedEspecialDiversos().getSeq();

		}

		MpmAltaOtrProcedimentoDAO dao = this.getAltaOtrProcedimentoDAO();
		MpmAltaOtrProcedimento altaOtrProcedimentoVelho = dao.obterAltaOtrProcedimentoOriginalDesatachado(altaOtrProcedimento);


		// RNU9 - Verifica se ALTA_SUMARIOS está ativo
		this.getAltaSumarioRN().verificarAltaSumarioAtivo(altaOtrProcedimento.getMpmAltaSumarios());

		//RNU 10 - VerificarSituacao this.verificarSituacao
		this.verificarSituacao(altaOtrProcedimento.getIndSituacao(), altaOtrProcedimentoVelho.getIndSituacao(), ManterAltaOtrProcedimentoRNExceptionCode.MPM_02629);
		//RNU12 - verificarIndCarga
		this.verificarIndCarga(altaOtrProcedimento.getIndCarga(), altaOtrProcedimentoVelho.getIndCarga(), ManterAltaOtrProcedimentoRNExceptionCode.MPM_02630 );

		this.verificarAltaOtrProcedimentoModificada(altaOtrProcedimento, altaOtrProcedimentoVelho);

		//RNU15 - Verifica se precisa preencher o compl_out_procedimento
		Integer matCodigo = altaOtrProcedimento.getMatCodigo() != null ? altaOtrProcedimento.getMatCodigo().getCodigo() : null;
		this.verificarCompl(
				altaOtrProcedimento.getComplOutProcedimento(),
				altaOtrProcedimento.getIndCarga(),
				pciSeq,
				pedSeq,
				matCodigo);

		//RNU17 - Verifica data de procedimento
		this.getAltaSumarioRN().verificarDtHr(altaOtrProcedimento.getId()
				.getAsuApaAtdSeq(), altaOtrProcedimento
				.getDthrOutProcedimento());

		this.verificarModificacaoAltaOtrProcedimento(altaOtrProcedimento, altaOtrProcedimentoVelho);

	}

	/**
	 * ORADB Trigger MPMT_OPC_BRU
	 * 
	 * @param {DominioSituacao} novoIndSituacao
	 * @param {DominioSituacao} antigoIndSituacao
	 * @throws ApplicationBusinessException
	 * Msg MPM_02629
	 */
	protected void verificarSituacao(DominioSituacao novoIndSituacao,
			DominioSituacao antigoIndSituacao,
			BusinessExceptionCode msgCodigoErro) throws ApplicationBusinessException {
		if (DominioSituacao.A.equals(novoIndSituacao)
				&& DominioSituacao.I.equals(antigoIndSituacao)) {
			throw new ApplicationBusinessException(msgCodigoErro);
		}
	}


	/**
	 * ORADB Procedure MPMT_OPC_BRU
	 * ORADB Procedure mpmk_opc_rn.rn_opcp_ver_ind_carg
	 * 
	 * Não deve permitir a alterar ind_carga. Se foi modificado o valor de
	 * ind_carga deve subir uma exceção com o código de erro passado por
	 * parâmetro.
	 * MPM-02630 
	 * @param {Boolean} novoIndCarga
	 * @param {Boolean} antigoIndCarga
	 * @param {BusinessExceptionCode} msgCodigoErro
	 * @throws ApplicationBusinessException
	 */
	protected void verificarIndCarga(Boolean novoIndCarga,
			Boolean antigoIndCarga, BusinessExceptionCode msgCodigoErro)
	throws ApplicationBusinessException {

		if (CoreUtil.modificados(novoIndCarga, antigoIndCarga)) {

			throw new ApplicationBusinessException(msgCodigoErro);

		}

	}
	//RNU11 - RN_OPCP_VER_UPDATE
	protected void verificarAltaOtrProcedimentoModificada(MpmAltaOtrProcedimento altaOtrProcedimentoNovo,MpmAltaOtrProcedimento altaOtrProcedimentoVelho) throws ApplicationBusinessException{

		if(DominioSimNao.N.equals(altaOtrProcedimentoNovo.getIndCarga())){

			if(((altaOtrProcedimentoNovo.getDescOutProcedimento()== null) && (altaOtrProcedimentoVelho.getDescOutProcedimento() == null) &&
					!CoreUtil.modificados(altaOtrProcedimentoNovo.getDescOutProcedimento(), altaOtrProcedimentoVelho.getDescOutProcedimento()) 
					&& (altaOtrProcedimentoNovo.getMbcProcedimentoCirurgicos() == null) && (altaOtrProcedimentoVelho.getMbcProcedimentoCirurgicos() == null)) 
					|| (CoreUtil.modificados(altaOtrProcedimentoNovo.getMbcProcedimentoCirurgicos(), altaOtrProcedimentoVelho.getMbcProcedimentoCirurgicos())	
					&&(((altaOtrProcedimentoNovo.getMpmProcedEspecialDiversos() == null) && (altaOtrProcedimentoVelho.getMpmProcedEspecialDiversos() == null)) ||
							CoreUtil.modificados(altaOtrProcedimentoNovo.getMpmProcedEspecialDiversos(), altaOtrProcedimentoVelho.getMpmProcedEspecialDiversos())	
					) && (((altaOtrProcedimentoNovo.getMatCodigo() == null) && (altaOtrProcedimentoVelho.getMatCodigo() == null)) || 
							CoreUtil.modificados(altaOtrProcedimentoNovo.getMatCodigo(), altaOtrProcedimentoVelho.getMatCodigo())
					))
			){

				throw new ApplicationBusinessException(
						ManterAltaOtrProcedimentoRNExceptionCode.MPM_02627);
			}
			if((altaOtrProcedimentoNovo.getMpmProcedEspecialDiversos() == null) && (altaOtrProcedimentoVelho.getMpmProcedEspecialDiversos() == null) 
					&& (altaOtrProcedimentoNovo.getMbcProcedimentoCirurgicos() == null) && (altaOtrProcedimentoVelho.getMbcProcedimentoCirurgicos() == null)
					&& (altaOtrProcedimentoNovo.getMatCodigo() == null) && (altaOtrProcedimentoVelho.getMatCodigo() == null)
					&& !CoreUtil.modificados(altaOtrProcedimentoNovo.getDescOutProcedimento(), altaOtrProcedimentoVelho.getDescOutProcedimento())
			){
				throw new ApplicationBusinessException(
						ManterAltaOtrProcedimentoRNExceptionCode.MPM_02627);
			}

			if(!CoreUtil.modificados(altaOtrProcedimentoNovo.getMbcProcedimentoCirurgicos(), altaOtrProcedimentoVelho.getMbcProcedimentoCirurgicos())
					&& !CoreUtil.modificados(altaOtrProcedimentoNovo.getMpmProcedEspecialDiversos(), altaOtrProcedimentoVelho.getMpmProcedEspecialDiversos())
					&& !CoreUtil.modificados(altaOtrProcedimentoNovo.getMatCodigo(), altaOtrProcedimentoVelho.getMatCodigo())
					&& CoreUtil.modificados(altaOtrProcedimentoNovo.getDescOutProcedimento(), altaOtrProcedimentoNovo.getDescOutProcedimento())
			){
				throw new ApplicationBusinessException(
						ManterAltaOtrProcedimentoRNExceptionCode.MPM_02628);
			}

		}
	}

	//MPMT_OPC_BRU - mpmk_opc_rn.rn_opcp_ver_modific
	protected void verificarModificacaoAltaOtrProcedimento(MpmAltaOtrProcedimento altaOtrProcedimentoNovo,MpmAltaOtrProcedimento altaOtrProcedimentoVelho) throws ApplicationBusinessException{

		if(
				CoreUtil.modificados(altaOtrProcedimentoNovo.getId(), altaOtrProcedimentoVelho.getId()) 
				|| CoreUtil.modificados(altaOtrProcedimentoNovo.getDescOutProcedimento(), altaOtrProcedimentoVelho.getDescOutProcedimento())
				|| CoreUtil.modificados(altaOtrProcedimentoNovo.getIndCarga(), altaOtrProcedimentoVelho.getIndCarga())
				|| CoreUtil.modificados(altaOtrProcedimentoNovo.getMpmProcedEspecialDiversos(), altaOtrProcedimentoVelho.getMpmProcedEspecialDiversos())
				|| CoreUtil.modificados(altaOtrProcedimentoNovo.getDthrOutProcedimento(), altaOtrProcedimentoVelho.getDthrOutProcedimento())
				|| CoreUtil.modificados(altaOtrProcedimentoNovo.getMbcProcedimentoCirurgicos(), altaOtrProcedimentoVelho.getMbcProcedimentoCirurgicos())
				|| CoreUtil.modificados(altaOtrProcedimentoNovo.getComplOutProcedimento(), altaOtrProcedimentoVelho.getComplOutProcedimento())
				|| CoreUtil.modificados(altaOtrProcedimentoNovo.getMatCodigo(), altaOtrProcedimentoVelho.getMatCodigo())
		){
			if(DominioSimNao.S.equals(altaOtrProcedimentoNovo.getIndCarga())){
				throw new ApplicationBusinessException(
						ManterAltaOtrProcedimentoRNExceptionCode.MPM_02660);
			}			
		}
	}

	public void atualizarAltaOtrProcedimento(MpmAltaOtrProcedimento altaOtrProcedimento)
	throws ApplicationBusinessException{
		this.preAtualizarAltaOtrProcedimento(altaOtrProcedimento);
		this.getAltaOtrProcedimentoDAO().merge(altaOtrProcedimento);
		this.getAltaOtrProcedimentoDAO().flush();
	}

	/**
	 * ORADB Trigger de delete MPMT_OPC_BRD.
	 * 
	 * @param altaOtrProcedimento
	 * @throws ApplicationBusinessException
	 */
	public void removerAltaOtrProcedimento(MpmAltaOtrProcedimento altaOtrProcedimento)
	throws ApplicationBusinessException{
		try {
			MpmAltaSumario altaSumario = altaOtrProcedimento.getMpmAltaSumarios();
			this.getAltaSumarioRN().verificarAltaSumarioAtivo(altaSumario);
			this.getAltaOtrProcedimentoDAO().remover(altaOtrProcedimento);
			this.getAltaOtrProcedimentoDAO().flush();
		} catch (Exception e) {
			logError("Exceção capturada: ", e);
			throw new ApplicationBusinessException(
					ManterAltaOtrProcedimentoRNExceptionCode.MPM_02818);
		}
	}
	
	protected ManterAltaSumarioRN getAltaSumarioRN() {
		return manterAltaSumarioRN;
	}

	protected MpmAltaOtrProcedimentoDAO getAltaOtrProcedimentoDAO() {
		return mpmAltaOtrProcedimentoDAO;
	}

}
