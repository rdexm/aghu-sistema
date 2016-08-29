package br.gov.mec.aghu.prescricaomedica.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MpmAltaRecomendacao;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaRecomendacaoDAO;
import br.gov.mec.aghu.prescricaomedica.vo.MpmAltaRecomendacaoVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * 
 * @author bsoliveira
 * 
 */
@Stateless
public class ManterAltaRecomendacaoRN extends BaseBusiness {


@EJB
private ManterAltaSumarioRN manterAltaSumarioRN;

private static final Log LOG = LogFactory.getLog(ManterAltaRecomendacaoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private MpmAltaRecomendacaoDAO mpmAltaRecomendacaoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 7881930035706822470L;

	public enum ManterAltaRecomendacaoRNExceptionCode implements
			BusinessExceptionCode {

		MPM_02690, MPM_02691;

		public void throwException(Object... params)
				throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}

	}

	/**
	 * Insere objeto MpmAltaRecomendacao.
	 * 
	 * @param altaRecomendacao
	 * @param primeiraMaiuscula
	 */
	public void inserirAltaRecomendacao(MpmAltaRecomendacao altaRecomendacao, Boolean primeiraMaiuscula)
			throws ApplicationBusinessException {

		this.preInserirAltaRecomendacao(altaRecomendacao);
		if(primeiraMaiuscula){
			montaDescricaoRecomendacaoComPrimeiraMaiuscula(altaRecomendacao);
		}
		this.getAltaRecomendacaoDAO().persistir(altaRecomendacao);
		this.getAltaRecomendacaoDAO().flush();

	}

	/**
	 * Insere objeto MpmAltaRecomendacao.
	 * 
	 * @param altaRecomendacao
	 */
	public void inserirAltaRecomendacao(MpmAltaRecomendacao altaRecomendacao)
			throws ApplicationBusinessException {
		inserirAltaRecomendacao(altaRecomendacao, true);
	}
	/**
	 * Atualiza objeto MpmAltaRecomendacao.
	 * 
	 * @param altaRecomendacao
	 */
	public void atualizarAltaRecomendacao(MpmAltaRecomendacao altaRecomendacao)
			throws ApplicationBusinessException {

		this.preAtualizarAltaRecomendacao(altaRecomendacao);
		montaDescricaoRecomendacaoComPrimeiraMaiuscula(altaRecomendacao);
		this.getAltaRecomendacaoDAO().merge(altaRecomendacao);
		this.getAltaRecomendacaoDAO().flush();

	}
	
	private void montaDescricaoRecomendacaoComPrimeiraMaiuscula(MpmAltaRecomendacao altaRecomendacao){
		if(altaRecomendacao != null && altaRecomendacao.getDescricao()!= null && altaRecomendacao.getDescricao().trim().length()>1){
			altaRecomendacao.setDescricao(altaRecomendacao.getDescricao().trim().substring(0,1).toUpperCase()+altaRecomendacao.getDescricao().trim().substring(1).toLowerCase());
		}
		
		if(altaRecomendacao != null && altaRecomendacao.getDescricaoSistema()!=null && altaRecomendacao.getDescricaoSistema().trim().length()>1){
			altaRecomendacao.setDescricaoSistema(altaRecomendacao.getDescricaoSistema().trim().substring(0,1).toUpperCase()+altaRecomendacao.getDescricaoSistema().trim().substring(1).toLowerCase());
		}
	}
	
	/**
	 * Remover objeto MpmAltaRecomendacao.
	 * 
	 * @param altaRecomendacao
	 */
	public void removerAltaRecomendacao(MpmAltaRecomendacao altaRecomendacao)
			throws ApplicationBusinessException {

		this.preRemoverAltaRecomendacao(altaRecomendacao);
		this.getAltaRecomendacaoDAO().remover(altaRecomendacao);
		this.getAltaRecomendacaoDAO().flush();

	}

	/**
	 * @ORADB Trigger MPMT_ARC_BRI
	 * 
	 * EXECUTA ANTES DE FAZER O INSERT DO OBJETO.
	 * 
	 * @param altaRecomendacao
	 */
	private void preInserirAltaRecomendacao(
			MpmAltaRecomendacao altaRecomendacao) throws ApplicationBusinessException {

		// verifica se alta sumário está ativo
		getAltaSumarioRN().verificarAltaSumarioAtivo(
				altaRecomendacao.getAltaSumario());

		// verifica tipo de alta do sumário
		getAltaSumarioRN().verificarTipoAlteracao(
				altaRecomendacao.getAltaSumario().getId());

	}

	/**
	 * @ORADB Trigger MPMT_ARC_BRU
	 * 
	 * EXECUTA ANTES DE FAZER O UPDATE DO OBJETO.
	 * 
	 * @param altaRecomendacao
	 */
	private void preAtualizarAltaRecomendacao(MpmAltaRecomendacao altaRecomendacao) throws ApplicationBusinessException {
		getAltaSumarioRN().verificarAltaSumarioAtivo(altaRecomendacao.getAltaSumario());

		getAltaSumarioRN().verificarTipoAlteracao(altaRecomendacao.getAltaSumario().getId());

		MpmAltaRecomendacaoVO atualAltaRecomendacao = getAltaRecomendacaoDAO().obterAltaRecomendacaoOriginal(altaRecomendacao);
		
		this.verificarAtualizacao(altaRecomendacao, atualAltaRecomendacao);

	}
	
	/**
	 * @ORADB Trigger MPMT_ARC_BRD
	 * 
	 * EXECUTA ANTES DE FAZER O DELETE DO OBJETO.
	 * 
	 * @param altaRecomendacao
	 */
	private void preRemoverAltaRecomendacao(
			MpmAltaRecomendacao altaRecomendacao) throws ApplicationBusinessException{

		getAltaSumarioRN().verificarAltaSumarioAtivo(
				altaRecomendacao.getAltaSumario());

	}

	/**
	 * @ORADB Package MPMK_ARC_RN
	 * @ORADB Procedure RN_ARCP_VER_ALTERA
	 * 
	 * mpmk_arc_rn.rn_arcp_ver_altera
	 * Operação: UPD
  	 * Descrição:Garantir que o atributo descricao sistema só possa ser alterado
     * se uma das FKs, SERV RECOMENDACAO ALTA, PRESCRICAO DIETA, PRESCRICAO CUIDADO,
     * PRESCRICAO MDTO, também for alterada.
     * Obrigar que este atributo seja alterado quando uma das fks
     * , SERV RECOMENDACAO ALTA, PRECRICAO DIETA, PRESCRICAO CUIDADO,
     * PRESCRICAO MDTO, for alterada. 
	 * 
	 * @param (MpmAltaRecomendacao) altaRecomendacao
	 * @param (MpmAltaRecomendacao) atualAltaRecomendacao
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	protected void verificarAtualizacao(MpmAltaRecomendacao altaRecomendacao,
			MpmAltaRecomendacaoVO atualAltaRecomendacao)
			throws ApplicationBusinessException {

		String novoDescSistema = altaRecomendacao.getDescricaoSistema();
		String atualDescSistema = atualAltaRecomendacao.getDescricaoSistema();
		String novoMatricula = altaRecomendacao.getServidorRecomendacaoAlta() != null ? altaRecomendacao
				.getServidorRecomendacaoAlta().getServidor()
				.getUsuario()
				: null;
		String atualMatricula = atualAltaRecomendacao
				.getServidorRecomendacaoAlta() != null ? atualAltaRecomendacao
				.getServidorRecomendacaoAlta().getServidor()
				.getUsuario() : null;
		String novoVinculo = altaRecomendacao.getServidorRecomendacaoAlta() != null ? altaRecomendacao
				.getServidorRecomendacaoAlta().getServidor()
				.getMatriculaVinculo() : null;
		String atualVinculo = atualAltaRecomendacao
				.getServidorRecomendacaoAlta() != null ? atualAltaRecomendacao
				.getServidorRecomendacaoAlta().getServidor()
				.getMatriculaVinculo() : null;
		Integer novoSeqp = altaRecomendacao.getServidorRecomendacaoAlta() != null ? altaRecomendacao
				.getServidorRecomendacaoAlta().getId().getSeqp()
				: null;
		Integer atualSeqp = atualAltaRecomendacao.getServidorRecomendacaoAlta() != null ? atualAltaRecomendacao
				.getServidorRecomendacaoAlta().getId().getSeqp()
				: null;
		Integer novoPdtAtdSeq = altaRecomendacao.getPdtAtdSeq();
		Integer atualPdtAtdSeq = atualAltaRecomendacao.getPdtAtdSeq();
		Integer novoPdtSeq = altaRecomendacao.getPdtSeq();
		Integer atualPdtSeq = atualAltaRecomendacao.getPdtSeq();
		Integer novoPcuAtdSeq = altaRecomendacao.getPcuAtdSeq();
		Integer atualPcuAtdSeq = atualAltaRecomendacao.getPcuAtdSeq();
		Integer novoPcuSeq = altaRecomendacao.getPcuSeq();
		Integer atualPcuSeq = atualAltaRecomendacao.getPcuSeq();
		Integer novoPmdSeq = altaRecomendacao.getPmdSeq();
		Integer atualPmdSeq = atualAltaRecomendacao.getPmdSeq();
		Integer novoPmdAtdSeq = altaRecomendacao.getPmdAtdSeq();
		Integer atualPmdAtdSeq = atualAltaRecomendacao.getPmdAtdSeq();

		if (CoreUtil.modificados(novoDescSistema, atualDescSistema)) {

			if (!CoreUtil.modificados(novoMatricula, atualMatricula)
					&& !CoreUtil.modificados(novoVinculo, atualVinculo)
					&& !CoreUtil.modificados(novoSeqp, atualSeqp)
					&& !CoreUtil.modificados(novoPdtAtdSeq, atualPdtAtdSeq)
					&& !CoreUtil.modificados(novoPdtSeq, atualPdtSeq)
					&& !CoreUtil.modificados(novoPcuAtdSeq, atualPcuAtdSeq)
					&& !CoreUtil.modificados(novoPcuSeq, atualPcuSeq)
					&& !CoreUtil.modificados(novoPmdSeq, atualPmdSeq)
					&& !CoreUtil.modificados(novoPmdAtdSeq, atualPmdAtdSeq)) {

				ManterAltaRecomendacaoRNExceptionCode.MPM_02690
						.throwException();

			}

		} else {

			if (CoreUtil.modificados(novoMatricula, atualMatricula)
					|| CoreUtil.modificados(novoVinculo, atualVinculo)
					|| CoreUtil.modificados(novoSeqp, atualSeqp)
					|| CoreUtil.modificados(novoPdtAtdSeq, atualPdtAtdSeq)
					|| CoreUtil.modificados(novoPdtSeq, atualPdtSeq)
					|| CoreUtil.modificados(novoPcuAtdSeq, atualPcuAtdSeq)
					|| CoreUtil.modificados(novoPcuSeq, atualPcuSeq)
					|| CoreUtil.modificados(novoPmdSeq, atualPmdSeq)
					|| CoreUtil.modificados(novoPmdAtdSeq, atualPmdAtdSeq)) {

				ManterAltaRecomendacaoRNExceptionCode.MPM_02691
						.throwException();

			}

		}

	}
	
	/**
	 * Busca os objetos MpmAltaRecomendacao associados ao MpmAltaSumario que sejam de Dieta, Cuidado e Medicamento. 
	 * Utilizado em: Itens Recomendados no Plano PosAlta a partir da Ultima Prescricao.
	 * 
	 * @param altaSumario
	 * @return
	 */
	public List<MpmAltaRecomendacao> buscaItensAltaRecomendacaoPrescricaoMedica(MpmAltaSumario altaSumario) {
		MpmAltaRecomendacaoDAO dao = this.getAltaRecomendacaoDAO();
		
		return dao.buscaItensAltaRecomendacaoPrescricaoMedica(altaSumario);
	}

	protected ManterAltaSumarioRN getAltaSumarioRN() {
		return manterAltaSumarioRN;
	}

	protected MpmAltaRecomendacaoDAO getAltaRecomendacaoDAO() {
		return mpmAltaRecomendacaoDAO;
	}

	public List<MpmAltaRecomendacao> obterMpmAltaRecomendacao(Integer altanAtdSeq, Integer altanApaSeq, Short altanAsuSeqp)
			throws ApplicationBusinessException {
		return this.getAltaRecomendacaoDAO().obterMpmAltaRecomendacao(
				altanAtdSeq, altanApaSeq, altanAsuSeqp, DominioSituacao.A);
	}

}
