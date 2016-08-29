package br.gov.mec.aghu.senior;



import br.gov.mec.aghu.exames.vo.ClienteNfeVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;


/**
 * Interface da Fachada para o modulo de integração do AGHU com sistema da senior cliente.
 * 
 * @author alisson.salin
 * 
 */
@SuppressWarnings("ucd")
public interface ISeniorService {
	
	
  public Long gravarClienteNota(ClienteNfeVO clienteNfeVo) throws ApplicationBusinessException;
  
}
