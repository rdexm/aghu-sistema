package br.gov.mec.aghu.procedimentoterapeutico.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.procedimentoterapeutico.dao.MptProtocoloCicloDAO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.MptProtocoloVersaoAssistencialVO;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class MptProtocoloCicloRN extends BaseBusiness {
	
	private static final long serialVersionUID = 4706387327328423461L;
	private static final Log LOG = LogFactory.getLog(MptProtocoloCicloRN.class);
	
	@Inject
	private MptProtocoloCicloDAO mptProtocoloCicloDAO;
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	public String pesquisarProtocoloGrid(Integer cloSeq){
		StringBuilder descricaoProtocolo = new StringBuilder();
		List<MptProtocoloVersaoAssistencialVO> listaProtocolo = mptProtocoloCicloDAO.pesquisarProtocoloGrid(cloSeq);
		int tamanho = listaProtocolo.size();
		int i = 0;
		if(listaProtocolo != null && !listaProtocolo.isEmpty()){
			if(listaProtocolo.get(0).getDescricao() != null){
				descricaoProtocolo.append(listaProtocolo.get(0).getDescricao());
			}else if (listaProtocolo.size() == 1){
				descricaoProtocolo.append(listaProtocolo.get(0).getTitulo());
			}else if(listaProtocolo.size() > 1){
				for(MptProtocoloVersaoAssistencialVO item:listaProtocolo){
					i++;					
					descricaoProtocolo.append(item.getTitulo());
					if(i != tamanho){
						descricaoProtocolo.append(" - ");						
					}
				}
			}
		}
		return descricaoProtocolo.toString();
	}

	public String nomeResponsavel(String nome1, String nome2) {
		if(nome1 != null && !nome1.trim().isEmpty()){
			return nome1;
		}else{
			return nome2;
		}		
	}
}
