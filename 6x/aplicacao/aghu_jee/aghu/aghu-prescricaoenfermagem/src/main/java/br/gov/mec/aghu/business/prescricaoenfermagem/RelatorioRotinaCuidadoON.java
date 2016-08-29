package br.gov.mec.aghu.business.prescricaoenfermagem;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.EpeCuidados;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeCuidadosDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class RelatorioRotinaCuidadoON extends BaseBusiness{


@Inject
private EpeCuidadosDAO epeCuidadosDAO;

private static final Log LOG = LogFactory.getLog(RelatorioRotinaCuidadoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}



	/**
	 * 
	 */
	private static final long serialVersionUID = -1235948313053660062L;

	public List<EpeCuidados> pesquisarEpeCuidadosPorCodigo(Short seq) {
		
		EpeCuidados epeCuidados = epeCuidadosDAO.obterCuidadosPrescricaoPorSeq(seq);

        if (epeCuidados != null) {
            if (epeCuidados.getRotina() != null) {
                epeCuidados.setRotina(epeCuidados.getRotina().replace("\t", "   "));
            }

            if (epeCuidados.getInformacoesAdicionais() != null){
                epeCuidados.setInformacoesAdicionais(epeCuidados.getInformacoesAdicionais().replace("\t", "   "));
            }
            List<EpeCuidados> cuidadosLista = new ArrayList<EpeCuidados>();
            cuidadosLista.add(epeCuidados);
            return cuidadosLista;
        }

        return null;
	}


	protected EpeCuidadosDAO getEpeCuidadosDAO() {
		return epeCuidadosDAO;
	}
}
