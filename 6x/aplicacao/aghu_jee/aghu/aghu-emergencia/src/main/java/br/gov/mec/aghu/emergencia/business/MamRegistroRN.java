package br.gov.mec.aghu.emergencia.business;

import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.dominio.DominioSituacaoRegistro;
import br.gov.mec.aghu.emergencia.dao.MamRegistrosDAO;
import br.gov.mec.aghu.emergencia.producer.QualificadorUsuario;
import br.gov.mec.aghu.model.MamRegistro;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.dao.RapServidoresDAO;
import br.gov.mec.aghu.registrocolaborador.vo.Usuario;
import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * Regras de negócio de MamRegistros
 * 
 * @author loleksinski
 * 
 */
@Stateless
public class MamRegistroRN extends BaseBusiness {
	private static final long serialVersionUID = -6781526900905284611L;

	@Inject
	private MamRegistrosDAO mamRegistrosDAO;
	
	@Inject
	@QualificadorUsuario
	private Usuario usuario;
	
	@Inject
	private RapServidoresDAO servidorDAO;

	@Override
	protected Log getLogger() {
		return null;
	}
	
	public void persistir(MamRegistro mamRegistro, String micNome) {
		if (mamRegistro.getSeq() == null){
			preInserir(mamRegistro, micNome);
			this.mamRegistrosDAO.persistir(mamRegistro);
		} else {
			this.mamRegistrosDAO.atualizar(mamRegistro);
		}
	}
	
	/**
	 * @ORADB MAM_REGISTROS.MAMT_RGT_BRI
	 * @param mamRegistro
	 */
	private void preInserir(MamRegistro mamRegistro, String micNome){
		mamRegistro.setCriadoEm(new Date());
		
		mamRegistro.setServidor(servidorDAO.obter(new RapServidoresId(usuario.getMatricula(),usuario.getVinculo())));
		
		mamRegistro.setMicNome(micNome);
		
		if (mamRegistro.getIndNoConsultorio() == null) {
			mamRegistro.setIndNoConsultorio(false);
		}
		
		if (mamRegistro.getIndSituacao() == null) {
			mamRegistro.setIndSituacao(DominioSituacaoRegistro.EE);
		}
	}
}
