package br.gov.mec.aghu.compras.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoFornecedorDAO;
import br.gov.mec.aghu.compras.dao.ScoHistoricoAdvertFornDAO;
import br.gov.mec.aghu.compras.dao.ScoHistoricoMultaFornDAO;
import br.gov.mec.aghu.compras.dao.ScoHistoricoOcorrFornDAO;
import br.gov.mec.aghu.compras.dao.ScoHistoricoSuspensFornDAO;
import br.gov.mec.aghu.dominio.DominioTipoFornecedor;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@SuppressWarnings("PMD.CyclomaticComplexity")
@Stateless
public class ManterFornecedorRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ManterFornecedorRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@EJB
	private IComprasFacade comprasFacade;

	@Inject
	private ScoFornecedorDAO scoFornecedorDAO;

	@Inject
	private ScoHistoricoSuspensFornDAO scoHistoricoSuspensFornDAO;

	@Inject
	private ScoHistoricoAdvertFornDAO scoHistoricoAdvertFornDAO;

	@Inject
	private br.gov.mec.aghu.compras.dao.ScoHistoricoOcorrFornDAO scoHistoricoOcorrFornDAO;

	@Inject
	private ScoHistoricoMultaFornDAO scoHistoricoMultaFornDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -3909414234582816144L;

	public enum ManterFornecedorRNExceptionCode implements BusinessExceptionCode {
		ERRO_USUARIO_NAO_ENCONTRADO, ERRO_CPF_CNPJ_INFORMADOS, ERRO_SEM_CNPJ_COM_INSCRICAO_ESTADUAL,
		ERRO_TIPO_FORNECEDOR_EXIGE_CPF_CNPJ, ERRO_TIPO_FORNECEDOR_NAO_EXISTE, ERRO_NUMERO_FORNECEDOR,
		ERRO_CNPJ_INVALIDO, ERRO_CADASTRO_FORNECEDOR_CNPJ_DUPLICADO, ERRO_CPF_INVALIDO, ERRO_CADASTRO_FORNECEDOR_CPF_DUPLICADO;
	}

	public ScoFornecedor inserirScoFornecedor(final ScoFornecedor fornecedor, final boolean gravar) throws ApplicationBusinessException {
		if (fornecedor == null) {
			return null;
		}
		
		verificarCNPJCPFJaCadastrado(fornecedor);
		
		ScoFornecedor fornecedorTemp = scotFrnBri(fornecedor);
		
		getComprasFacade().persistirScoMaterial(fornecedorTemp);
		
		final ScoFornecedor retorno = scotFrnAri(fornecedorTemp);
		if (gravar) {
			flush();
		}
		return retorno;
	}

	private void verificarCNPJCPFJaCadastrado(final ScoFornecedor fornecedor) throws ApplicationBusinessException {
		ScoFornecedor fornAux = new ScoFornecedor();//fornecedor só para busca de cnpj e cpf
		
		fornAux.setCgc(fornecedor.getCgc());
		fornAux.setCpf(fornecedor.getCpf());
		
		String cpfCnpj = "";
		
		if(fornecedor.getCgc() != null) {
			cpfCnpj = "00000000000000" + fornecedor.getCgc().toString();
			cpfCnpj = cpfCnpj.substring(cpfCnpj.length() - 14);
			
		} else if(fornecedor.getCpf() != null) {
			cpfCnpj = "00000000000" + fornecedor.getCpf().toString();
			cpfCnpj = cpfCnpj.substring(cpfCnpj.length() - 11);
		}
		
		List<ScoFornecedor> list = getComprasFacade().pesquisarFornecedorCompleta(0, 999999, null, false, fornAux, cpfCnpj);
		
		//Remove o próprio fornecedor, para os casos de edição
		list.remove(fornecedor);
		
		if (DominioTipoFornecedor.FNA.equals(fornecedor.getTipoFornecedor()) && fornecedor.getCgc() != null && list.size() > 0) {
			throw new ApplicationBusinessException(ManterFornecedorRNExceptionCode.ERRO_CADASTRO_FORNECEDOR_CNPJ_DUPLICADO);
		}
		else if (DominioTipoFornecedor.FNA.equals(fornecedor.getTipoFornecedor()) && fornecedor.getCpf() != null && list.size() > 0) {
			throw new ApplicationBusinessException(ManterFornecedorRNExceptionCode.ERRO_CADASTRO_FORNECEDOR_CPF_DUPLICADO);
		}
	}

	public ScoFornecedor atualizarScoFornecedor(final ScoFornecedor fornecedor, final ScoFornecedor oldFornecedor, final boolean gravar) throws ApplicationBusinessException {
		
		verificarCNPJCPFJaCadastrado(fornecedor);
		
		final ScoFornecedor retorno = getComprasFacade().atualizarScoFornecedor(scotFrnBru(fornecedor, oldFornecedor));

		if(gravar){
			flush();
		}	
		return retorno;
	}

	/**
	 * Insere ou atualiza de acordo com o número do fornecedor. (nulo = novo, preenchido = atualizacao)
	 * @param fornecedor
	 * @param gravar - Flush ou não
	 * @param oldFornecedor - Só necessário em atualização, para novo pode informar nulo.
	 * @return
	 * @throws ApplicationBusinessException
	 * @author bruno.mourao
	 * @since 26/03/2012
	 */
	public ScoFornecedor persistirScoFornecedor(final ScoFornecedor fornecedor, final boolean gravar) throws ApplicationBusinessException{
		if(fornecedor.getNumero() == null){
			return inserirScoFornecedor(fornecedor, gravar);
		}
		else{
			ScoFornecedor oldFornecedor = getComprasFacade().obterOriginalScoFornecedor(fornecedor);
			return atualizarScoFornecedor(fornecedor, oldFornecedor, gravar);
		}
	}

	/**
	 * 
	 * ORADB Trigger SCOT_FRN_BRI
	 * 
	 * @param fornecedor
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private ScoFornecedor scotFrnBri(final ScoFornecedor fornecedor) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if (servidorLogado == null) {
			throw new ApplicationBusinessException(ManterFornecedorRNExceptionCode.ERRO_USUARIO_NAO_ENCONTRADO);
		}
		verificaCpfCgc(fornecedor);
		// TODO parametrizar "TIPO_FORN". Ou não!
		if (StringUtils.isNotBlank(fornecedor.getClassificacao()) && !scocCheckReferenceCode(fornecedor.getClassificacao(), "TIPO_FORN")) {
			throw new ApplicationBusinessException(ManterFornecedorRNExceptionCode.ERRO_TIPO_FORNECEDOR_NAO_EXISTE);
		}
		fornecedor.setServidor(servidorLogado);
		// Retirado para a "scotFrnAri" para ter um "numero"
		// if (DominioTipoFornecedor.FNE.equals(fornecedor.getTipoFornecedor()))
		// {
		// fornecedor.setCgc(fornecedor.getNumero().longValue());
		// }
		return fornecedor;
	}

	/**
	 * Implementado a partir de parte da "scotFrnBri" para setar o valor de
	 * "numero" no CGC para fornecedores estrangeiros.
	 * 
	 * @param fornecedor
	 * @return
	 */
	private ScoFornecedor scotFrnAri(final ScoFornecedor fornecedor) {
		if (DominioTipoFornecedor.FNE.equals(fornecedor.getTipoFornecedor())) {
			fornecedor.setCgc(fornecedor.getNumero().longValue());
		}
		return fornecedor;
	}

	private void verificaCpfCgc(final ScoFornecedor fornecedor) throws ApplicationBusinessException {
		if (fornecedor.getCpf() != null && fornecedor.getCgc() != null) {
			throw new ApplicationBusinessException(ManterFornecedorRNExceptionCode.ERRO_CPF_CNPJ_INFORMADOS);
		}
		if (fornecedor.getCgc() == null && StringUtils.isNotBlank(fornecedor.getInscricaoEstadual())) {
			throw new ApplicationBusinessException(ManterFornecedorRNExceptionCode.ERRO_SEM_CNPJ_COM_INSCRICAO_ESTADUAL);
		}
		if (fornecedor.getCpf() == null && fornecedor.getCgc() == null && DominioTipoFornecedor.FNA.equals(fornecedor.getTipoFornecedor())) {
			throw new ApplicationBusinessException(ManterFornecedorRNExceptionCode.ERRO_TIPO_FORNECEDOR_EXIGE_CPF_CNPJ);
		}
		if (DominioTipoFornecedor.FNA.equals(fornecedor.getTipoFornecedor())){
			if(fornecedor.getCgc() != null) {
				if (!CoreUtil.validarCNPJ(fornecedor.getCgc().toString())) {
					throw new ApplicationBusinessException(ManterFornecedorRNExceptionCode.ERRO_CNPJ_INVALIDO);
				}
			}
			else if(fornecedor.getCpf() != null){
				if (!CoreUtil.validarCPF(fornecedor.getCpf().toString())) {
					throw new ApplicationBusinessException(ManterFornecedorRNExceptionCode.ERRO_CPF_INVALIDO);
				}
			}
		}
	}

	/**
	 * 
	 * ORADB Trigger SCOT_FRN_BRU
	 * 
	 * @param fornecedor
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private ScoFornecedor scotFrnBru(final ScoFornecedor fornecedor, final ScoFornecedor oldFornecedor) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		// TODO Revisar esse if
		if (!fornecedor.getNumero().equals(oldFornecedor.getNumero())) {
			throw new ApplicationBusinessException(ManterFornecedorRNExceptionCode.ERRO_NUMERO_FORNECEDOR);
		}
		// TODO parametrizar matricula ignorada
		if (servidorLogado.getId().getMatricula() != 9999999) {
			if (servidorLogado.getId().getMatricula() == null) {
				throw new ApplicationBusinessException(ManterFornecedorRNExceptionCode.ERRO_USUARIO_NAO_ENCONTRADO);
			}
			fornecedor.setServidor(servidorLogado);
		}
		if (!CoreUtil.igual(fornecedor.getCgc(), oldFornecedor.getCgc()) || !CoreUtil.igual(fornecedor.getCpf(), oldFornecedor.getCpf())
				|| !CoreUtil.igual(fornecedor.getInscricaoEstadual(), oldFornecedor.getInscricaoEstadual())) {
			verificaCpfCgc(fornecedor);
		}
		// TODO parametrizar "TIPO_FORN". Ou não!
		if (StringUtils.isNotBlank(fornecedor.getClassificacao()) && !CoreUtil.igual(fornecedor.getClassificacao(), oldFornecedor.getClassificacao())
				&& !scocCheckReferenceCode(fornecedor.getClassificacao(), "TIPO_FORN")) {
			throw new ApplicationBusinessException(ManterFornecedorRNExceptionCode.ERRO_TIPO_FORNECEDOR_NAO_EXISTE);
		}
		return fornecedor;
	}

	/**
	 * ORADB FUNCTION scoc_check_reference_code
	 * 
	 * @return
	 */
	public boolean scocCheckReferenceCode(final String classicicacao, final String tipoDominio) {
		final Long val = getComprasFacade().validarScoRefCodesPorTipoOperConversao(classicicacao, tipoDominio);
		return val != null && val > 0;
	}
	
	protected ScoFornecedorDAO getScoFornecedorDAO() {		
		return scoFornecedorDAO;
	}

	protected IComprasFacade getComprasFacade() {
		return this.comprasFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
	// #23370 - RN01
		public int obterNumeroPenalidades(final ScoFornecedor fornecedor) {
			int resultado = 0;
			
			resultado += obterNumeroPenalidadesExcetoOcorrencias(fornecedor);
			
			// C3
			resultado += obterNumeroOcorrencias(fornecedor);
			
			return resultado;
		}
		
		// #23370 - RN01
		public int obterNumeroPenalidadesExcetoOcorrencias(final ScoFornecedor fornecedor) {
			int resultado = 0;
			
			// C1
			resultado += obterNumeroSuspensoes(fornecedor);
		
			// C2
			resultado += obterNumeroAdvertencias(fornecedor);
					
			// C4
			resultado += obterNumeroMulta(fornecedor);
					
			return resultado;
		}
		
		// C1
		public long obterNumeroSuspensoes(final ScoFornecedor fornecedor) {
			return getScoHistoricoSuspensFornDAO().listarHistoricoSuspensFornCount(fornecedor.getNumero());
		}
		
		// C2
		public long obterNumeroAdvertencias(final ScoFornecedor fornecedor) {
			return getScoHistoricoAdvertFornDAO().listarHistoricoAdvertFornCount(fornecedor.getNumero());
		}
		
		// C3
		public long obterNumeroOcorrencias(final ScoFornecedor fornecedor) {
			return getScoHistoricoOcorrFornDAO().listarHistoricoOcorrFornCount(fornecedor.getNumero());
		}
		
		// C4
		public long obterNumeroMulta(final ScoFornecedor fornecedor) {
			return getScoHistoricoMultaFornDAO().listarHistoricoMultaFornCount(fornecedor.getNumero());
		}

		public Integer obterProximoNumeroCrcFornecedor(ScoFornecedor fornecedor) {
			
			Integer resultado = null;
			if ((fornecedor.getCrc() == null) && (fornecedor.getDtValidadeCrc() != null)) {
				resultado = (getScoFornecedorDAO().obterMaxForcenecor() + 1);
			}
			return resultado;
		}	
		
		protected ScoHistoricoSuspensFornDAO getScoHistoricoSuspensFornDAO(){
			return scoHistoricoSuspensFornDAO;
		}
		
		protected ScoHistoricoAdvertFornDAO getScoHistoricoAdvertFornDAO(){
			return scoHistoricoAdvertFornDAO;
		}
		
		
		protected ScoHistoricoOcorrFornDAO getScoHistoricoOcorrFornDAO(){
			return scoHistoricoOcorrFornDAO;
		}
		
		protected ScoHistoricoMultaFornDAO getScoHistoricoMultaFornDAO(){
			return scoHistoricoMultaFornDAO;
		}		
}
