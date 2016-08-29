package br.gov.mec.aghu.faturamento.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.faturamento.dao.FatItemContaHospitalarDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ContaProcedProfissionalVinculoIncorretoRN extends BaseBusiness {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 12684436130354894L;


	private static final Log LOG = LogFactory.getLog(ContaProcedProfissionalVinculoIncorretoRN.class);
	
	
	@Inject
	private FatItemContaHospitalarDAO fatItemContaHospitalarDAO;

	@EJB
	private IParametroFacade parametroFacade;
	
	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	private enum ContaProcedProfissionalVinculoIncorretoRNException implements BusinessExceptionCode {
		NENHUM_REGISTRO_PVI
	}
	
	
	//	Executar a consulta C1.
	//	Os dados retornados deverão ser gravados em um arquivo CSV nomeado como: VINCULO_INCORRETO_<datahoraatual>.
	//	Onde: 
	//	O termo <datahoraatual> refere-se à data e hora do momento da geração do arquivo no formato ddmmyyyyhh24mi.
	//	O cabeçalho (primeira linha a ser gravada no arquivo) deverá conter a seguinte informação: Procedimentos vinculo incorreto
	//	Após geração do arquivo, exibir a mensagem MS01 com a quantidade de registros retornados e a quantidade de arquivos retornados.
	//	Caso a consulta C1 não retorne resultados exibir a mensagem MS02.
	//RN1
	public List<Integer> buscarContasProcedProfissionalVinculoIncorreto() throws ApplicationBusinessException {
		List<Integer> resultado = null;
		DominioSituacaoConta[] situacoesConta =  new DominioSituacaoConta[] {DominioSituacaoConta.A,  DominioSituacaoConta.F,  DominioSituacaoConta.E};
		Short[] vinculos = buscarVinculosIncorreto();
		resultado = fatItemContaHospitalarDAO.buscarContasProcedProfissionalVinculoIncorreto(vinculos, situacoesConta);
		if (resultado == null || resultado.isEmpty()) {
			throw new ApplicationBusinessException(ContaProcedProfissionalVinculoIncorretoRNException.NENHUM_REGISTRO_PVI);
		}
		return resultado;
	}
	
	
	
	private Short[] buscarVinculosIncorreto() throws ApplicationBusinessException {
		Short[] valores = parametroFacade.buscarValorShortArray(AghuParametrosEnum.P_VINCULO_INCORRETO);
		if (valores == null || valores.length == 0) {
			return null;
		}
		return valores;
	}

}
