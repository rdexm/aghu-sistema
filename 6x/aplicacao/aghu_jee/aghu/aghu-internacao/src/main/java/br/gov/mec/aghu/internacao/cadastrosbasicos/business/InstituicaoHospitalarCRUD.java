package br.gov.mec.aghu.internacao.cadastrosbasicos.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.controleinfeccao.business.IControleInfeccaoFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AelDadosCadaveres;
import br.gov.mec.aghu.model.AghInstituicoesHospitalares;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.MciMvtoInfeccaoTopografias;
import br.gov.mec.aghu.model.MciMvtoMedidaPreventivas;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responsável por prover os métodos de negócio genéricos para a entidade
 * de Acomodação.
 */
@Stateless
public class InstituicaoHospitalarCRUD extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(InstituicaoHospitalarCRUD.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IAghuFacade aghuFacade;

@EJB
private IInternacaoFacade internacaoFacade;

@EJB
private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

@EJB
private IControleInfeccaoFacade controleInfeccaoFacade;


	/**
	 * 
	 */
	private static final long serialVersionUID = -4474361027934735689L;


	private enum InstituicaoHospitalarCRUDExceptionCode implements
			BusinessExceptionCode {
		ERRO_CIDADE_CADASTRADA_E_NAO_CADASTRADA,ERRO_PERSISTIR_INSTITUICAO, ERRO_PERSISTIR_INSTITUICAO_CONSTRAINT, DESCRICAO_INSTITUICAO_OBRIGATORIO, DESCRICAO_INSTITUICAO_JA_EXISTENTE, ERRO_REMOVER_INSTITUICAO_CONSTRAINT_CONTAS_HOSPITAIS_DIA, ERRO_REMOVER_INSTITUICAO_CONSTRAINT_CONTAS_INTERNACOES, ERRO_REMOVER_INSTITUICAO_CONSTRAINT_CONTAS_HOSPITALARES, ERRO_REMOVER_INSTITUICAO, NOME_INSTITUICAO_OBRIGATORIO, ERRO_INSTITUICAO_NAO_CADASTRADA, ERRO_REMOVER_INSTITUICAO_CONSTRAINT_INST_HOSP_TEMP, ERRO_REMOVER_INSTITUICAO_CONSTRAINT_AEL_DADOS_CADAVERES, ERRO_REMOVER_INSTITUICAO_CONSTRAINT_AIN_INTERNACOES, ERRO_REMOVER_INSTITUICAO_CONSTRAINT_MCI_MVTO_INFECCAO_TOPOGRAFIAS, ERRO_REMOVER_INSTITUICAO_CONSTRAINT_MCI_MVTO_MEDIDA_PREVENTIVAS, ERRO_INSTITUICAO_CIDADE_CADASTRADA_OU_NAO_CADASTRADA_OBRIGATORIO, ERRO_JA_EXISTE_INSTITUICAO_LOCAL_CADASTRADA;
	}


	/**
	 * Método responsável pela persistência de uma Instituição Hospitalar.
	 * 
	 * @param Instituição
	 *            Hospitalar
	 * @throws ApplicationBusinessException
	 */
	public void persistirInstituicao(final AghInstituicoesHospitalares instituicao)
			throws ApplicationBusinessException {
		this.validarDadosInstituicao(instituicao);
		if (instituicao.getSeq() == null) {
			// inclusão
			this.getAghuFacade().inserirAghInstituicoesHospitalares(instituicao, false);
		} else{
			//edição
			this.getAghuFacade().atualizarAghInstituicoesHospitalares(instituicao, false);
		}
	}


	/**
	 * Método responsável por validar campos.
	 * 
	 * @param instituicao
	 * @throws ApplicationBusinessException
	 */
	private void validarDadosInstituicao(final AghInstituicoesHospitalares instituicao)
			throws ApplicationBusinessException {
		// validar se o campo nome ou descrição esta preenchido
		if (StringUtils.isBlank(instituicao.getNome())) {
			throw new ApplicationBusinessException(
					InstituicaoHospitalarCRUDExceptionCode.NOME_INSTITUICAO_OBRIGATORIO);
		}

		if ((instituicao.getCddCodigo() == null && instituicao.getUfSigla() == null)
				|| (instituicao.getCddCodigo() != null && instituicao.getUfSigla() != null)) {
			throw new ApplicationBusinessException(InstituicaoHospitalarCRUDExceptionCode.ERRO_INSTITUICAO_NAO_CADASTRADA);
		}

		if (instituicao.getCddCodigo() == null && StringUtils.isBlank(instituicao.getCidade())) {
			throw new ApplicationBusinessException(
					InstituicaoHospitalarCRUDExceptionCode.ERRO_INSTITUICAO_CIDADE_CADASTRADA_OU_NAO_CADASTRADA_OBRIGATORIO);

		}
		
		
		if (instituicao.getCddCodigo() != null && !StringUtils.isBlank(instituicao.getCidade())) {
			throw new ApplicationBusinessException(
					InstituicaoHospitalarCRUDExceptionCode.ERRO_CIDADE_CADASTRADA_E_NAO_CADASTRADA);

		}
		
		// Verifica se já existe uma instituição definida como local
		if (instituicao.getIndLocal()
				&& verificarInstituicaoLocal(instituicao.getSeq())) {
			throw new ApplicationBusinessException(
					InstituicaoHospitalarCRUDExceptionCode.ERRO_JA_EXISTE_INSTITUICAO_LOCAL_CADASTRADA);
		}

	}
	
	/**
	 * Método responsável pela remoção de uma Acomodação.
	 * 
	 * @param instituicao
	 * @throws ApplicationBusinessException
	 */
	@Secure("#{s:hasPermission('instituicaoHospitalar','excluir')}")
	public void removerInstituicao(final AghInstituicoesHospitalares instituicao)
			throws ApplicationBusinessException {
		verificarRegrasExclusão(instituicao);
		this.getAghuFacade().removerAghInstituicoesHospitalares(instituicao, true);

	}
	
	/**
	 * Verifica se pode excluir o objeto instituição hospitalar
	 * @param instituicao
	 * @throws AGHUNegocioException 
	 */
	private void verificarRegrasExclusão(AghInstituicoesHospitalares instituicao) throws ApplicationBusinessException {
		IInternacaoFacade internacaoFacade = getInternacaoFacade();
		//Internações (Origem e Transferência)
		List<AinInternacao> listaInternacoesOrigem = internacaoFacade
				.pesquisarInternacoesInstituicaoHospitalarOrigem(instituicao.getSeq());
		List<AinInternacao> listaInternacoesTransferencia = internacaoFacade
				.pesquisarInternacoesInstituicaoHospitalarTransferencia(instituicao.getSeq());
		if (!listaInternacoesOrigem.isEmpty() || !listaInternacoesTransferencia.isEmpty()){
			throw new ApplicationBusinessException(
					InstituicaoHospitalarCRUDExceptionCode.ERRO_REMOVER_INSTITUICAO_CONSTRAINT_AIN_INTERNACOES);
		}
		
		//Dados Cadaveres (Procedencia e Retirada)
		List<AelDadosCadaveres> listaDadosCadaveresProcedencia = getCadastrosApoioExamesFacade()
				.pesquisarDadosCadaveresInstituicaoHospitalarProcedencia(
						instituicao.getSeq());
		List<AelDadosCadaveres> listaDadosCadaveresRetirada = getCadastrosApoioExamesFacade()
				.pesquisarDadosCadaveresInstituicaoHospitalarRetirada(
						instituicao.getSeq());
		if (!listaDadosCadaveresProcedencia.isEmpty() || !listaDadosCadaveresRetirada.isEmpty()){
			throw new ApplicationBusinessException(
					InstituicaoHospitalarCRUDExceptionCode.ERRO_REMOVER_INSTITUICAO_CONSTRAINT_AEL_DADOS_CADAVERES);
		}
		
		//Movimentos Infecção Topografia
		List<MciMvtoInfeccaoTopografias> listaMvtoInfeccaoTopografia = getControleInfeccaoFacade()
				.pesquisarMovimentosInfeccoesInstituicaoHospitalar(
						instituicao.getSeq());
		if (!listaMvtoInfeccaoTopografia.isEmpty()){
			throw new ApplicationBusinessException(
					InstituicaoHospitalarCRUDExceptionCode.ERRO_REMOVER_INSTITUICAO_CONSTRAINT_MCI_MVTO_INFECCAO_TOPOGRAFIAS);
		}
		
		//Movimentos de Medida Preventiva
		List<MciMvtoMedidaPreventivas> listaMvtoMedidasPreventivas = getControleInfeccaoFacade()
				.pesquisarMovimentosMedidasPreventivasInstituicaoHospitalar(
						instituicao.getSeq());
		if (!listaMvtoMedidasPreventivas.isEmpty()){
			throw new ApplicationBusinessException(
					InstituicaoHospitalarCRUDExceptionCode.ERRO_REMOVER_INSTITUICAO_CONSTRAINT_MCI_MVTO_MEDIDA_PREVENTIVAS);
		}
	}


	/**
	 * Indica qual a instituição está usando o sistema.
	 * 
	 * @return
	 */
	public String recuperarNomeInstituicaoLocal() {	

		final String nome = this.getAghuFacade().recuperarNomeInstituicaoLocal();

		if (nome != null) {
			return WordUtils.capitalizeFully(nome).replaceAll(" Da ", " da ")
					.replaceAll(" De ", " de ").replaceAll(" Do ", " do ");
		} else {
			return "";
		}

	}
	

	/**
	 * Verifica se já existe instituição definida como local
	 * 
	 * @param seq
	 * @return
	 */
	private boolean verificarInstituicaoLocal(final Integer seq) {		
		
		final AghInstituicoesHospitalares instituicao = this.getAghuFacade().verificarInstituicaoLocal(seq);

		if (instituicao != null) {
			return true;
		}

		return false;
	}	
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}


	protected IInternacaoFacade getInternacaoFacade() {
		return internacaoFacade;
	}


	protected void setInternacaoFacade(IInternacaoFacade internacaoFacade) {
		this.internacaoFacade = internacaoFacade;
	}


	protected ICadastrosApoioExamesFacade getCadastrosApoioExamesFacade() {
		return cadastrosApoioExamesFacade;
	}


	protected void setCadastrosApoioExamesFacade(ICadastrosApoioExamesFacade cadastrosApoioExamesFacade) {
		this.cadastrosApoioExamesFacade = cadastrosApoioExamesFacade;
	}


	protected IControleInfeccaoFacade getControleInfeccaoFacade() {
		return controleInfeccaoFacade;
	}


	protected void setControleInfeccaoFacade(IControleInfeccaoFacade controleInfeccaoFacade) {
		this.controleInfeccaoFacade = controleInfeccaoFacade;
	}


	protected void setAghuFacade(IAghuFacade aghuFacade) {
		this.aghuFacade = aghuFacade;
	}
	
	

}
