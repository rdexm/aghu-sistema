package br.gov.mec.aghu.emergencia.business;

import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.dominio.DominioSituacaoRegistro;
import br.gov.mec.aghu.emergencia.dao.MamExtratoRegistrosDAO;
import br.gov.mec.aghu.emergencia.producer.QualificadorUsuario;
import br.gov.mec.aghu.model.MamExtratoRegistro;
import br.gov.mec.aghu.model.MamExtratoRegistroId;
import br.gov.mec.aghu.registrocolaborador.dao.RapServidoresDAO;
import br.gov.mec.aghu.registrocolaborador.vo.Usuario;

/**
 * Regras de negócio de MamExtratoRegistros
 * 
 * @author loleksinski
 * 
 */
@Stateless
public class MamExtratoRegistroRN extends BaseBusiness {
	private static final long serialVersionUID = -6781526900905284611L;

	@Inject
	private MamExtratoRegistrosDAO mamExtratoRegistrosDAO;
	
	@Inject
	private RapServidoresDAO rapServidoresDAO;
	
	@Inject
	@QualificadorUsuario
	private Usuario usuario;

	@Override
	protected Log getLogger() {
		return null;
	}
	
	public void persistir(MamExtratoRegistro mamExtratoRegistro, Long rgtSeq, String micNome) {
		if (mamExtratoRegistro.getId() == null){
			mamExtratoRegistro.setId(popularIdMamExtratoRegistro(rgtSeq));
			preInserir(mamExtratoRegistro, micNome);
			this.mamExtratoRegistrosDAO.persistir(mamExtratoRegistro);
		} else {
			this.mamExtratoRegistrosDAO.atualizar(mamExtratoRegistro);
		}
	}
	
	/**
	 * @ORADB MAM_EXTRATO_REGISTROS.MAMT_EXR_BRI
	 * @param mamRegistro
	 */
	private void preInserir(MamExtratoRegistro mamExtratoRegistro, String micNome){
		mamExtratoRegistro.setCriadoEm(new Date());		
		//mamExtratoRegistro.setSerMatricula(usuario.getMatricula());
		//mamExtratoRegistro.setSerVinCodigo(usuario.getVinculo());
		mamExtratoRegistro.setServidor(rapServidoresDAO.obterRapServidorPorVinculoMatricula(usuario.getMatricula(), usuario.getVinculo()));
		mamExtratoRegistro.setMicNome(micNome);
		
		if (mamExtratoRegistro.getIndSituacao() == null) {
			mamExtratoRegistro.setIndSituacao(DominioSituacaoRegistro.EE);
		}
	}
	
	private MamExtratoRegistroId popularIdMamExtratoRegistro(Long rgtSeq) {
		MamExtratoRegistroId id = new MamExtratoRegistroId();
		id.setRgtSeq(rgtSeq);
		id.setSeqp(this.mamExtratoRegistrosDAO.obterMaxSeqpPorRgtSeq(rgtSeq));
		return id;
	}

}
