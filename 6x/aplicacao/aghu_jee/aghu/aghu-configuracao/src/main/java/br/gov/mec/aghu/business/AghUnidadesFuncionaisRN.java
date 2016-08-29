package br.gov.mec.aghu.business;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.configuracao.dao.AghUnidadesFuncionaisDAO;
import br.gov.mec.aghu.model.AghTiposUnidadeFuncional;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;



@Stateless
public class AghUnidadesFuncionaisRN extends BaseBusiness {
	
	private static final Log LOG = LogFactory.getLog(AghUnidadesFuncionaisRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	@Inject
	private AghUnidadesFuncionaisDAO aghUnidadesFuncionaisDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6113311983851897909L;
	
	private AghUnidadesFuncionaisDAO getAghUnidadesFuncionaisDAO(){
		return aghUnidadesFuncionaisDAO;
	}
	

	public void atualizarAghUnidadesFuncionais(AghUnidadesFuncionais aghUnidadesFuncionais) throws ApplicationBusinessException{
			
		if(aghUnidadesFuncionais.getHrioInicioAtendimento() != null &&  aghUnidadesFuncionais.getHrioFimAtendimento() != null){	
			validarHora(aghUnidadesFuncionais.getHrioInicioAtendimento(), aghUnidadesFuncionais.getHrioFimAtendimento());
		}else if( (aghUnidadesFuncionais.getHrioInicioAtendimento() != null &&  aghUnidadesFuncionais.getHrioFimAtendimento() == null) ||
				(aghUnidadesFuncionais.getHrioInicioAtendimento() == null &&  aghUnidadesFuncionais.getHrioFimAtendimento() != null)){
			AghUnidadesFuncionaisRNExceptionCode.UNID_FUNC_ERRO_001.throwException();
		}
		getAghUnidadesFuncionaisDAO().atualizar(aghUnidadesFuncionais);
		getAghUnidadesFuncionaisDAO().flush();
	}
	
	public enum AghUnidadesFuncionaisRNExceptionCode implements BusinessExceptionCode {
		ERRO_DATA_INICIAL_MAIOR, UNID_FUNC_ERRO_001;

		public void throwException(Object... params) throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}
	}
	
	private void validarHora(Date dataInicial, Date dataFinal) throws ApplicationBusinessException {
		
		if (DateUtil.validaHoraMenorIgual(dataFinal, dataInicial)) {
			
			AghUnidadesFuncionaisRNExceptionCode.ERRO_DATA_INICIAL_MAIOR.throwException();
			
		}
		
	}
	
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoEDescricaoPorTipoUnidadeFuncional(
			Object parametro,
			AghTiposUnidadeFuncional aghTiposUnidadeFuncional, Integer maxResult) {
		List<AghUnidadesFuncionais> lista = this
				.getAghUnidadesFuncionaisDAO()
				.pesquisarUnidadeFuncionalPorCodigoEDescricaoPorTipoUnidadeFuncional(
						parametro, aghTiposUnidadeFuncional, maxResult);
		
		for (AghUnidadesFuncionais aghUnidadesFuncionais : lista) {
			aghUnidadesFuncionais.getAndarAlaDescricao();//Como a consulta é um criteria, precisa chamar o método para carregar a referência da ala
		}
		
		return lista;
	}
	
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoEDescricao(
			String strPesquisa, Boolean ordernarPorCodigoAlaDescricao,
			boolean apenasAtivos,
			boolean caracteristicasInternacaoOuEmergencia,
			boolean caracteristicaUnidadeExecutora) {
		
		List<AghUnidadesFuncionais> lista =  this.getAghUnidadesFuncionaisDAO()
				.pesquisarUnidadeFuncionalPorCodigoEDescricao(strPesquisa,
						ordernarPorCodigoAlaDescricao, apenasAtivos,
						caracteristicasInternacaoOuEmergencia,
						caracteristicaUnidadeExecutora);
		
		for (AghUnidadesFuncionais aghUnidadesFuncionais : lista) {
			aghUnidadesFuncionais.getAndarAlaDescricao();//Como a consulta é um criteria, precisa chamar o método para carregar a referência da ala
		}
		
		return lista;
	}

}
