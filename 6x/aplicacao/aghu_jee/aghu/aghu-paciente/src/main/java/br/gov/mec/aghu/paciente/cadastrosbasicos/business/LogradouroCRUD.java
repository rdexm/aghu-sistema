package br.gov.mec.aghu.paciente.cadastrosbasicos.business;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.AipBairros;
import br.gov.mec.aghu.model.AipBairrosCepLogradouro;
import br.gov.mec.aghu.model.AipBairrosCepLogradouroId;
import br.gov.mec.aghu.model.AipCepLogradouros;
import br.gov.mec.aghu.model.AipLogradouros;
import br.gov.mec.aghu.model.AipTipoLogradouros;
import br.gov.mec.aghu.model.AipTituloLogradouros;
import br.gov.mec.aghu.paciente.dao.AipBairrosCepLogradouroDAO;
import br.gov.mec.aghu.paciente.dao.AipBairrosDAO;
import br.gov.mec.aghu.paciente.dao.AipLogradourosDAO;
import br.gov.mec.aghu.paciente.dao.AipTipoLogradourosDAO;
import br.gov.mec.aghu.paciente.dao.AipTituloLogradourosDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class LogradouroCRUD extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(LogradouroCRUD.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	@Inject
	private AipTituloLogradourosDAO aipTituloLogradourosDAO;
	
	@Inject
	private AipTipoLogradourosDAO aipTipoLogradourosDAO;
	
	@Inject
	private AipBairrosCepLogradouroDAO aipBairrosCepLogradouroDAO;
	
	@Inject
	private AipLogradourosDAO aipLogradourosDAO;
	
	@Inject
	private AipBairrosDAO aipBairrosDAO;

	private static final long serialVersionUID = -7748118353903401745L;

	private enum LogradouroExceptionCode implements BusinessExceptionCode {
		ERRO_REMOVER_TIPO_LOGRADOURO, ERRO_REMOVER_TITULO_LOGRADOURO, ERRO_PERSISTIR_TIPO_LOGRADOURO, ERRO_PERSISTIR_TITULO_LOGRADOURO,
		ERRO_PERSISTIR_LOGRADOURO, ERRO_REMOVER_TIPO_LOGRADOURO_ASSOCIADO_A_LOGRADOURO, ERRO_EXCEDEU_LIMITE_TIPO_LOGRADOURO_CADASTRADOS,
		ERRO_REMOVER_LOGRADOURO_REGISTROS_DEPENDENTES, ERRO_REMOVER_LOGRADOURO, ERRO_REMOVER_TITULO_LOGRADOURO_ASSOCIADO_A_LOGRADOURO, 
		ERRO_INESPERADO_OPERACAO_BASE_DADOS, ERRO_INESPERADO_OPERACAO, ERRO_DESCRICAO_TIPO_LOGRADOURO_EXISTENTE, 
		ERRO_ABREVIATURA_TIPO_LOGRADOURO_EXISTENTE, AIP_00100, AIP_00017, ERRO_EXCLUIR_BAIRRO_CEP_LOGRADOURO_COM_DEPENDENTES, ERRO_SALVAR_BAIRROS_CEP_IGUAIS,
		ERRO_CEP_LOGRADOURO_DUPLICADO, MSG_JA_EXISTE_TITULO_LOGRADOURO_COM_ESSA_DESCRICAO;
		
		public void throwException() throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this);
		}
		
		public void throwException(Throwable cause, Object... params) throws ApplicationBusinessException {
			// Tratamento adicional para não esconder a excecao de negocio original
			if (cause instanceof ApplicationBusinessException) {
				throw (ApplicationBusinessException) cause;
			}
			throw new ApplicationBusinessException(this, cause, params);
		}		
		
	}

	public void persistirLogradouro(AipLogradouros aipLogradouro) throws ApplicationBusinessException {
		final String nome = aipLogradouro.getNome();
		aipLogradouro.setNome(nome.toUpperCase());
		
		this.validaRelacionamentoComCidadeQueCadastraLogradouro(aipLogradouro);
		
		boolean novoRegistro = aipLogradouro.getCodigo() == null;

		if (novoRegistro) {
			this.getAipLogradourosDAO().persistir(aipLogradouro);			
		} else{
			this.getAipLogradourosDAO().atualizar(aipLogradouro);
		}
		atualizarEntidadesRelacionadasLogradouro(aipLogradouro);
	}
	
	private void validaRelacionamentoComCidadeQueCadastraLogradouro(AipLogradouros aipLogradouro) throws ApplicationBusinessException {
		if(aipLogradouro.getAipCidade().getIndLogradouro() == null || !aipLogradouro.getAipCidade().getIndLogradouro()){
			throw new ApplicationBusinessException(LogradouroExceptionCode.AIP_00017);
		}
	}
	

	private void atualizarEntidadesRelacionadasLogradouro(AipLogradouros aipLogradouro) throws ApplicationBusinessException {
		final Integer lgrCodigo = aipLogradouro.getCodigo();
		final Set<AipCepLogradouros> aipCepLogradouros = aipLogradouro.getAipCepLogradouros();	
		
		Set<Integer> setCeps = new HashSet<Integer>();
		for (AipCepLogradouros aipCepLogradouro : aipCepLogradouros) {
			aipCepLogradouro.getId().setLgrCodigo(lgrCodigo);
			aipCepLogradouro.setLogradouro(aipLogradouro);
			
			if (aipCepLogradouro.getBairroCepLogradouros().isEmpty()) {
				throw new ApplicationBusinessException(LogradouroExceptionCode.AIP_00100);
			}
			
			final int cep = aipCepLogradouro.getId().getCep();
			if (setCeps.contains(cep)) {
				throw new ApplicationBusinessException(LogradouroExceptionCode.ERRO_CEP_LOGRADOURO_DUPLICADO, cep);
			} else {
				setCeps.add(cep);
			}
			for (AipBairrosCepLogradouro aipBCL : aipCepLogradouro.getBairroCepLogradouros()) {
				aipBCL.getId().setCloLgrCodigo(lgrCodigo);
				aipBCL.getId().setCloCep(cep);				
				aipBCL.setCepLogradouro(aipCepLogradouro);   
				
				final Integer baiCodigo = aipBCL.getId().getBaiCodigo();
				if (aipBCL.getAipBairro() == null) {
					AipBairros aipBairro = this.getAipBairrosDAO().obterPorChavePrimaria(baiCodigo);
					aipBCL.setAipBairro(aipBairro);
				}
			}
		}
		
	}
	

	public void persistirTipoLogradouro(AipTipoLogradouros tipoLogradouro)throws ApplicationBusinessException {
		this.validarDadosTipoLogradouro(tipoLogradouro);
		if (tipoLogradouro.getCodigo() == null) {
			this.incluirTipoLogradouro(tipoLogradouro);
		} else {
			aipTipoLogradourosDAO.atualizar(tipoLogradouro);
		}
		aipTipoLogradourosDAO.flush();
	}
	

	/**
	 * Método responsável pelas validações dos dados de ocupação. Método
	 * utilizado para inclusão e atualização de ocupação.
	 * 
	 * @param ocupacao
	 * @throws ApplicationBusinessException
	 */
	private void validarDadosTipoLogradouro(AipTipoLogradouros tipoLogradouro)
			throws ApplicationBusinessException {
		// validar abreviatura duplicada
		List<AipTipoLogradouros> tl = getAipTipoLogradourosDAO().getTiposLogradouroComMesmaAbreviatura(tipoLogradouro);
		if (tl != null && !tl.isEmpty()) {
			throw new ApplicationBusinessException(LogradouroExceptionCode.ERRO_ABREVIATURA_TIPO_LOGRADOURO_EXISTENTE);
		}

		// validar descrição duplicada
		tl = getAipTipoLogradourosDAO().getTiposLogradouroComMesmaDescricao(tipoLogradouro);
		if (tl != null && !tl.isEmpty()) {
			throw new ApplicationBusinessException(LogradouroExceptionCode.ERRO_DESCRICAO_TIPO_LOGRADOURO_EXISTENTE);
		}
	}
	

	public void incluirTipoLogradouro(AipTipoLogradouros tipoLogradouro)throws ApplicationBusinessException {
		this.getAipTipoLogradourosDAO().persistir(tipoLogradouro);
	}

	
	public void removerTipoLogradouro(AipTipoLogradouros tipoLogradouro) throws ApplicationBusinessException {
		
		tipoLogradouro = this.getAipTipoLogradourosDAO().merge(tipoLogradouro);
		if (tipoLogradouro.getAipLogradouros() != null && tipoLogradouro.getAipLogradouros().size() > 0) {
			throw new ApplicationBusinessException(LogradouroExceptionCode.ERRO_REMOVER_TIPO_LOGRADOURO_ASSOCIADO_A_LOGRADOURO);
		}
		
		aipTipoLogradourosDAO.remover(tipoLogradouro);
		aipTipoLogradourosDAO.flush();
	}
	
	
	public void persistirTituloLogradouro(AipTituloLogradouros tituloLogradouro) throws ApplicationBusinessException {
		
		if (tituloLogradouro.getCodigo() == null) {
			
			this.getAipTituloLogradourosDAO().persistir(tituloLogradouro);
			
		} else { 
			
			this.getAipTituloLogradourosDAO().atualizar(tituloLogradouro);
			
		}
		
	}

	
	public void removerTituloLogradouro(AipTituloLogradouros tituloLogradouro) throws ApplicationBusinessException {
		tituloLogradouro = this.getAipTituloLogradourosDAO().merge(tituloLogradouro);
		if(tituloLogradouro.getAipLogradouros() != null && tituloLogradouro.getAipLogradouros().size() > 0){
			throw new ApplicationBusinessException(LogradouroExceptionCode.ERRO_REMOVER_TITULO_LOGRADOURO_ASSOCIADO_A_LOGRADOURO);
		}
		this.getAipTituloLogradourosDAO().remover(tituloLogradouro);
	}

	
	public void excluirLogradouro(Integer codigoLogradouro) throws ApplicationBusinessException {
		AipLogradouros aipLogradouro = getAipLogradourosDAO().obterPorChavePrimaria(codigoLogradouro);
		if (aipLogradouro.getAipCepLogradouros().isEmpty()) {
			this.getAipLogradourosDAO().remover(aipLogradouro);
		} else {
			LogradouroExceptionCode.ERRO_REMOVER_LOGRADOURO_REGISTROS_DEPENDENTES.throwException();
		}
	}

	
	public void excluirBairroCepLogradouro(Integer codigoLogradouro, Integer cep, Integer codigoBairro) throws ApplicationBusinessException {
		
		// Obs.: Eliminar merges, ocorrem automaticamente ao executar o flush
		AipBairrosCepLogradouroId aipBairrosCepLogradouroId = new AipBairrosCepLogradouroId(codigoLogradouro, cep, codigoBairro);
		AipBairrosCepLogradouro aipBairrosCepLogradouro = getAipBairrosCepLogradouroDAO().obterPorChavePrimaria(aipBairrosCepLogradouroId);
		
		AipCepLogradouros cepLogradouro = aipBairrosCepLogradouro.getCepLogradouro();
		cepLogradouro.getBairroCepLogradouros().remove(aipBairrosCepLogradouro);
		
		if (cepLogradouro.getBairroCepLogradouros().isEmpty()) {
			AipLogradouros aipLogradouro = cepLogradouro.getLogradouro();
			Set<AipCepLogradouros> aipCepLogradouros = aipLogradouro.getAipCepLogradouros();
			
			aipCepLogradouros.remove(cepLogradouro);
			if (aipCepLogradouros.isEmpty()) {
				this.getAipLogradourosDAO().remover(aipLogradouro);
			}				
		}	
	}
	
	public AipLogradouros obterLogradouroPorCodigoComCeps(Integer codigo){
		AipLogradouros logradouro = getAipLogradourosDAO().obterPorChavePrimaria(codigo);
		logradouro.getAipCepLogradouros().size();
		for (AipCepLogradouros cepLogradouro  : logradouro.getAipCepLogradouros()) {
			cepLogradouro.getBairroCepLogradouros().size();
			for (AipBairrosCepLogradouro bairrosCepLogradouro : cepLogradouro.getBairroCepLogradouros()) {
				bairrosCepLogradouro.getAipBairro().getCodigo();
				bairrosCepLogradouro.getAipBairro().getDescricao();
			}
		}
		return logradouro;
	}
	
	protected AipLogradourosDAO getAipLogradourosDAO(){
		return aipLogradourosDAO;
	}
	
	protected AipBairrosDAO getAipBairrosDAO(){
		return aipBairrosDAO;
	}
	
	protected AipTipoLogradourosDAO getAipTipoLogradourosDAO(){
		return aipTipoLogradourosDAO;
	}
	
	protected AipTituloLogradourosDAO getAipTituloLogradourosDAO(){
		return aipTituloLogradourosDAO;
	}
	
	protected AipBairrosCepLogradouroDAO getAipBairrosCepLogradouroDAO(){
		return aipBairrosCepLogradouroDAO;
	}
	
}